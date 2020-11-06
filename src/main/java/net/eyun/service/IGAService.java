package net.eyun.service;

import net.eyun.GA.GA;

import java.util.List;

/**
 * Created by songb
 */
public interface IGAService {
    List<GA> selectGa();

    void insertGa(GA ga);
}
