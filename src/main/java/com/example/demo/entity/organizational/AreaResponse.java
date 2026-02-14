package com.example.demo.entity.organizational;

import lombok.Data;

@Data
public class AreaResponse {
    private Long id;
    private String locationDetails;
    private String areaName;

    public AreaResponse(Long id, String areaName,String locationDetails ) {
        this.id = id;
        this.locationDetails = locationDetails;
        this.areaName = areaName;
    }


    public static  AreaResponse fromTable(Area item){
        return new AreaResponse (item.getId(),item.getAreaName(),item.getLocationDetails());
    }


    public static  Area ToTable(AreaResponse item){
        return new Area (item.getId(),item.getAreaName(),item.getLocationDetails());
    }


}

