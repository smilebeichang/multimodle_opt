package net.sysu.controller;

import net.sysu.GA.GA;
import net.sysu.service.IGAService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Created by songb
 */
@Controller
@RequestMapping("/ga")
@CrossOrigin("*")
public class GAController {
    @Resource
    private IGAService gaService;

    static double[] best_solution = new double[300];
    static double[] minnum=new double[300];

    @RequestMapping(value = "/insertAndSelectGa",method = {RequestMethod.GET})
    @ResponseBody
    public List<GA> insertAndSelectGa(@ModelAttribute("Ant") GA ga){
        ga.init();      //500个个体   300Iteration  .7  .05
        //300代迭代
        for (int i = 0; i < ga.getGenerations(); i++) {
            ga.spiltlist();
            ga.decoding();
            //计算适应度，并赋值给fitness[]
            ga.cfitness();
            //计算一次最小值
            Object[] objects = ga.best_value();
            best_solution[i] = Double.parseDouble(objects[1].toString());
            //genetic_population_1利用原值并产生新值
            ga.selection();
            ga.crosssover();
            ga.change();
            //精英策略
            ga.elitiststrategy();

            //算出来的是的适应度   //f2
            minnum[i]=1030-best_solution[i];

            ga.setIteration(i+1);
            ga.setMinnum2(minnum[i]);
            gaService.insertGa(ga);
        }


        List<GA> pageInfo = gaService.selectGa();
            return  pageInfo;
    }



}
