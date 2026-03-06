package com.github.dreamw4lker.simplexvalfx.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@AllArgsConstructor
@Getter
public class ProtocolTypeVersionBean {
    private String code;
    private String name;
    private List<Path> xsdFilenames;
    private List<Path> schematronFilenames;
}
