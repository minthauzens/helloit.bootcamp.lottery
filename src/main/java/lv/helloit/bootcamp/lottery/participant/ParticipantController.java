package lv.helloit.bootcamp.lottery.participant;

import lombok.extern.slf4j.Slf4j;
import lv.helloit.bootcamp.lottery.utils.Response;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
public class ParticipantController {
    private final ParticipantService participantService;
    private final ParticipantValidator participantValidator;

    public ParticipantController(ParticipantService participantService, ParticipantValidator participantValidator) {
        this.participantService = participantService;
        this.participantValidator = participantValidator;
    }


    @GetMapping("/public/register")
    public String registerParticipant(Model model) {
        log.info("accessing /public/register");
        model.addAttribute("participantRegisterDto", new ParticipantRegisterDto());
        return "register-participant";
    }

    @PostMapping("/public/register")
    public String registerParticipant(@Valid @ModelAttribute ParticipantRegisterDto participantRegisterDto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        log.info("trying to register new participant");
        if (bindingResult.hasErrors()) {
            String wrongField = bindingResult.getFieldErrors().get(0).getField();
            model.addAttribute(wrongField + "_err", true);
            model.addAttribute("participantRegisterDto", participantRegisterDto);
            log.info("failed");
            return "register-participant";
        }
        ValidatorResponse response = this.participantValidator.validate(participantRegisterDto);
        if (response.hasErrors()) {
            model.addAttribute("error_message", response.getMessage());
            log.info("failed: " + response.getMessage());
            return "register-participant";
        }
        this.participantService.createParticipant(participantRegisterDto);
        redirectAttributes.addAttribute("success_message", "Successfully registered!");
        return "redirect:/public/";
    }

    @GetMapping("/public/status")
    public String getParticipantStatus(Model model) {
        log.info("accessing /public/status");
        model.addAttribute("participantStatusDto", new ParticipantStatusDto());
        return "status";
    }

    @PostMapping("/public/status")
    public String getParticipantStatus(@Valid @ModelAttribute ParticipantStatusDto participantStatusDto,
                                       BindingResult bindingResult,
                                       Model model) {
        log.info("trying to get participant status (" + participantStatusDto + ")");
        if (bindingResult.hasErrors()) {
            String wrongField = bindingResult.getFieldErrors().get(0).getField();
            model.addAttribute(wrongField + "_err", true);
            log.info("failed: invalid data");
            return "status";
        }
        Response response = this.participantService.getParticipantStatus(participantStatusDto);
        if (response.hasErrors()) {
            log.info("failed: " + response.getMessage());
            model.addAttribute("error_message", response.getMessage());
        } else {
            log.info("SUCCESS. status:" + response.getMessage());
            model.addAttribute("participant_status", response.getMessage());
        }
        return "status";
    }
}
