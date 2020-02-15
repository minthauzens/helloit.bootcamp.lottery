package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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
        return "register-participant";
    }

    @PostMapping("/public/register")
    public String registerParticipant(@Valid @ModelAttribute ParticipantRegisterDto participantRegisterDto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String wrongField = bindingResult.getFieldErrors().get(0).getField();
            model.addAttribute(wrongField + "_err", true);

            model.addAttribute("participantDto", participantRegisterDto);
            return "register-participant";
        }
        ValidatorResponse response = this.participantValidator.validate(participantRegisterDto);
        if (response.hasErrors()) {
            model.addAttribute("error_message", response.getMessage());
            return "register-participant";
        }
        this.participantService.createParticipant(participantRegisterDto);
        redirectAttributes.addAttribute("success_message", "Successfully registered!");
        return "redirect:/";
    }

    @GetMapping("/public/status")
    public String getParticipantStatus(Model model) {
        return "status";
    }

    @PostMapping("/public/status")
    public String getParticipantStatus(@Valid @ModelAttribute ParticipantStatusDto participantStatusDto,
                                       BindingResult bindingResult,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            String wrongField = bindingResult.getFieldErrors().get(0).getField();
            model.addAttribute(wrongField + "_err", true);

            model.addAttribute("participantStatusDto", participantStatusDto);
            return "register-participant";
        }
        ResponseEntity<String> responseEntity = this.participantService.getParticipantStatus(participantStatusDto);
        model.addAttribute("participant_status", responseEntity.getBody());
        return "status";
    }
}
