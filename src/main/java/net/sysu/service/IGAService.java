package net.sysu.service;

import net.sysu.GA.GA;

import java.util.List;

/**
 * Created by songb
 */
public interface IGAService {
    List<GA> selectGa();

    void insertGa(GA ga);
}
