package lv.helloit.bootcamp.lottery.lottery;

import lombok.extern.slf4j.Slf4j;
import lv.helloit.bootcamp.lottery.participant.Participant;
import lv.helloit.bootcamp.lottery.participant.ParticipantService;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller()
public class LotteryController {
    private final LotteryService lotteryService;
    private final ParticipantService participantService;
    private final LotteryValidator lotteryValidator;

    public LotteryController(LotteryService lotteryService, ParticipantService participantService, LotteryValidator lotteryValidator) {
        this.lotteryService = lotteryService;
        this.participantService = participantService;
        this.lotteryValidator = lotteryValidator;
    }

    @GetMapping("/public/")
    public String index() {
        log.info("accessing /public/");
        return "index";
    }

    @GetMapping("/admin/lotteries")
    public String getAllLotteries(Model model) {
        log.info("authorized acceess to /admin/lotteries");
        model.addAttribute("lotteries", this.lotteryService.findAll());
        return "lotteries";
    }

    @GetMapping("/admin/lottery/{id}")
    public String getLottery(@PathVariable("id") long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        log.info("authorized acceess to /admin/lottery/" + id);
        Optional<Lottery> optionalLottery = this.lotteryService.findById(id);
        if (optionalLottery.isEmpty()) {
            redirectAttributes.addAttribute("error_message", "Lottery with such id doesn't exist");
            log.info("such lottery doesn't exist");
            return "redirect:/admin/lotteries";
        }
        model.addAttribute("participant_count", participantService.countParticipantsByLotteryId(id));
        model.addAttribute("lottery", optionalLottery.get());
        return "lottery";
    }

    @GetMapping("/admin/lottery/{id}/endRegistration")
    public String stopRegistration(@PathVariable("id") long id,
                                   RedirectAttributes redirectAttributes) {
        log.info("authorized access to /admin/{" + id + "}/endRegistration", id);
        log.info("Trying to end registration for lottery with id " + id);
        String url = "redirect:/admin/lottery/" + id;
        ValidatorResponse response = this.lotteryValidator.validateForStopRegistration(LotteryIdDto.builder().id(id).build());
        if (response.hasErrors()) {
            log.info("fail: " + response.getMessage());
            redirectAttributes.addAttribute("error_message", response.getMessage());
            return url;
        }
        redirectAttributes.addAttribute("success_message", "Lottery registration has been stopped");
        this.lotteryService.stopRegistration(id);
        return url;
    }

    @GetMapping("/admin/lottery/{id}/chooseWinner")
    public String chooseWinner(@PathVariable("id") long id,
                               RedirectAttributes redirectAttributes) {
        log.info("authorized access to /admin/{" + id + "}/chooseWinner", id);
        log.info("Choosing winner for lottery with id " + id);
        String url = "redirect:/admin/lottery/" + id;
        ValidatorResponse response = this.lotteryValidator.validateForChooseWinner(LotteryIdDto.builder().id(id).build());
        if (response.hasErrors()) {
            log.info("fail: " + response.getMessage());
            redirectAttributes.addAttribute("error_message", response.getMessage());
            return url;
        }
        if (!(this.participantService.existsByLotteryId(id))) {
            redirectAttributes.addAttribute("error_message", "This lottery has no participants");
            return url;
        }
        Participant participant = this.participantService.chooseLotteryWinner(id);
        this.lotteryService.setLotteryCompleted(id);
        String winnerCode = participant.getCode();

        redirectAttributes.addAttribute("success_message", "The lucky winner is with code: " + winnerCode);
        return url;
    }

    @GetMapping("/admin/createLottery")
    public String createLottery(Model model) {
        log.info("authorized acess to /admin/createLottery");
        model.addAttribute("lotteryRegistrationDto", new LotteryRegistrationDto());
        return "create-lottery";
    }

    @PostMapping("/admin/createLottery")
    public String createLottery(@Valid @ModelAttribute LotteryRegistrationDto lotteryRegistrationDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.info("trying to create a lottery POST:/admin/createLottery");
        if (bindingResult.hasErrors()) {
            String wrongField = bindingResult.getFieldErrors().get(0).getField();
            model.addAttribute(wrongField + "_err", true);

            model.addAttribute("lotteryRegistrationDto", lotteryRegistrationDto);
            log.info("failed!");
            return "create-lottery";
        }
        ValidatorResponse response = this.lotteryValidator.validateForRegistration(lotteryRegistrationDto);
        if (response.hasErrors()) {
            model.addAttribute("error_message", response.getMessage());
            log.info("failed: " + response.getMessage());
            return "create-lottery";
        }
        lotteryService.createLottery(lotteryRegistrationDto);
        redirectAttributes.addAttribute("success_message", "Successfully created Lottery: " + lotteryRegistrationDto.getTitle() + "!");
        return "redirect:/admin/lotteries";
    }

}
