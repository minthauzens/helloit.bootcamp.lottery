package lv.helloit.bootcamp.lottery.lottery;

import lv.helloit.bootcamp.lottery.participant.Participant;
import lv.helloit.bootcamp.lottery.participant.ParticipantService;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

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
        return "index";
    }

    @GetMapping("/admin/lotteries")
    public String getAllLotteries(Model model) {
        model.addAttribute("lotteries", this.lotteryService.findAll());
        return "lotteries";
    }

    @GetMapping("/admin/lottery/{id}")
    public String getLottery(@PathVariable("id") long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<Lottery> optionalLottery = this.lotteryService.findById(id);
        if (optionalLottery.isEmpty()) {
            redirectAttributes.addAttribute("error_message", "Lottery with such id doesn't exist");
            return "redirect:/admin/lotteries";
        }
        model.addAttribute("participant_count", participantService.countParticipantsByLotteryId(id));
        model.addAttribute("lottery", optionalLottery.get());
        return "lottery";
    }

    @GetMapping("/admin/lottery/{id}/endRegistration")
    public String stopRegistration(@PathVariable("id") long id,
                                   RedirectAttributes redirectAttributes) {
        String url = "redirect:/admin/lottery/" + id;
        ValidatorResponse response = this.lotteryValidator.validateForStopRegistration(LotteryIdDto.builder().id(id).build());
        if (response.hasErrors()) {
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
        String url = "redirect:/admin/lottery/" + id;

        ValidatorResponse response = this.lotteryValidator.validateForChooseWinner(LotteryIdDto.builder().id(id).build());
        if (response.hasErrors()) {
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

}
