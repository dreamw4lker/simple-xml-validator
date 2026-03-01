package com.github.dreamw4lker.simplexvalfx.beans;

import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class ProtocolTypeVersionConverter extends StringConverter<ProtocolTypeVersionBean> {
    private final Map<String, ProtocolTypeVersionBean> map = new HashMap<>();

    @Override
    public String toString(ProtocolTypeVersionBean protocolTypeVersionBean) {
        map.put(protocolTypeVersionBean.getName(), protocolTypeVersionBean);
        return protocolTypeVersionBean.getName();
    }

    @Override
    public ProtocolTypeVersionBean fromString(String name) {
        return map.get(name);
    }
}
