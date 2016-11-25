package com.estafet.training.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Delcho Delov on 21.11.2016 г..
 */
public final class IbanWrapper implements Serializable {
    private List<String> ibans;

    public List<String> getIbans() {
        return ibans;
    }

//    public void setIbans(List<String> ibans) {
//        this.ibans = ibans;
//    }
}
