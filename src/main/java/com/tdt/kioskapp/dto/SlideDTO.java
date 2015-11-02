package com.tdt.kioskapp.dto;

import lombok.Builder;
import lombok.Data;

/**
 * SlideDTO
 */
@Data
@Builder
public class SlideDTO {

    protected String location;
    protected int duration;
}
