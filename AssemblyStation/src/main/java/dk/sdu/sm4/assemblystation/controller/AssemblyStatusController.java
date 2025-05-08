package dk.sdu.sm4.assemblystation.controller;

import dk.sdu.sm4.assemblystation.model.AssemblyStatus;
import dk.sdu.sm4.assemblystation.service.CallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assembly")
public class AssemblyStatusController {

    private final CallbackHandler callbackHandler;

    @Autowired
    public AssemblyStatusController(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    @GetMapping("/status")
    public AssemblyStatus getStatus() {
        return callbackHandler.getLatestStatus();
    }
}