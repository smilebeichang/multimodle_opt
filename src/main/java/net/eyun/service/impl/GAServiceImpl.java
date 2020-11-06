package net.eyun.service.impl;

import net.eyun.GA.GA;
import net.eyun.mapper.GAMapper;
import net.eyun.service.IGAService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by songb
 */
@Service
public class GAServiceImpl implements IGAService {
    @Resource
    private GAMapper gaMapper;
    @Override
    public List<GA> selectGa() {
        return gaMapper.selectGa();
    }

    @Override
    public void insertGa(GA ga) {
        gaMapper.insertGa(ga);
    }
}
