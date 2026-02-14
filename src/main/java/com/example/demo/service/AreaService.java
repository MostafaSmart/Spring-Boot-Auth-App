package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.organizational.Area;
import com.example.demo.entity.organizational.AreaResponse;
import com.example.demo.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository repository;

    public List<AreaResponse> getAll() {
        return repository.findAll().stream()
                .map(this::fromTable)
                .collect(Collectors.toList());

    }

    public AreaResponse getById(Long id) {
        Area area = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("المنطقة غير موجودة"));
        return fromTable(area);
    }


    public AreaResponse save(Area area) {
        if (repository.existsByAreaName(area.getAreaName())) {
            throw new DataAlreadyExistsException("اسم المنطقة موجود بالفعل");
        }
        return fromTable(repository.save(area));
    }

    public AreaResponse update(Long id, Area area) {
        AreaResponse existingArea = getById(id);

        if (!existingArea.getAreaName().equals(area.getAreaName()) && repository.existsByAreaName(area.getAreaName())) {
            throw new DataAlreadyExistsException("اسم المنطقة موجود بالفعل");
        }

        var result =  repository.save(new Area(id, area.getAreaName(), area.getLocationDetails()));

        return  fromTable(result);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("المنطقة غير موجودة");
        }
        repository.deleteById(id);
    }




    private    AreaResponse fromTable(Area item){
        return new AreaResponse (item.getId(),item.getAreaName(),item.getLocationDetails());
    }


    private   Area ToTable(AreaResponse item){
        return new Area (item.getId(),item.getAreaName(),item.getLocationDetails());
    }

}
