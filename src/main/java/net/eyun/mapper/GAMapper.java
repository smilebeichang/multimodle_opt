package net.eyun.mapper;

import net.eyun.GA.GA;

import java.util.List;

/**
 * Created by songb
 */
public interface GAMapper {
    List<GA> selectGa();

    void insertGa(GA ga);
}
