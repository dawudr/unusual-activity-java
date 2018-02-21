package com.financialjuice.unusualactivity.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path="api/symbol") // This means URL's start with /stock (after Application path)
public class SymbolController {


}
