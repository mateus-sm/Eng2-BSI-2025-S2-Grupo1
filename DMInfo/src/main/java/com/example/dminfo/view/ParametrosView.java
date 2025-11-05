package com.example.dminfo.view;

import com.example.dminfo.controller.ParametrosController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/parametrizacao")
public class ParametrosView {

    @Autowired
    private ParametrosController parametros;

}