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
            ga.cfitness();           //计算适应度，并赋值给fitness[]
            //计算一次最小值
            Object[] objects = ga.best_value();
            best_solution[i] = Double.parseDouble(objects[1].toString());
            ga.selection();           //genetic_population_1利用原值并产生新值
            ga.crosssover();
            ga.change();
            ga.elitiststrategy();     //精英策略


            //算出来的是的适应度
            //minnum[i]=30000-best_solution[i];     //f1

            minnum[i]=1030-best_solution[i];        //f2

            //minnum[i]=150000-best_solution[i];    //f3

            //minnum[i]=100-best_solution[i];      //f4

            //minnum[i]=500000-best_solution[i];      //f5

            //minnum[i]=30000-best_solution[i];      //f6

            //minnum[i]=10000-best_solution[i];       //f8

            //minnum[i]=100000-best_solution[i];       //f9/f10



            ga.setIteration(i+1);
            ga.setMinnum2(minnum[i]);
            gaService.insertGa(ga);
        }
        //预期best_solution[i]越来越接近30000
        /*for (int i = 0; i < ga.getGenerations(); i++) {
            System.out.println("3minnum["+i+"]:"+minnum[i]+"   ");

        }*/

        List<GA> pageInfo = gaService.selectGa();
            return  pageInfo;
    }


    public List<GA> inAndSel(){
        GA ga = new GA();

        ga.init();      //500个个体   300Iteration  .7  .05
        //300代迭代
        for (int i = 0; i < ga.getGenerations(); i++) {
            ga.spiltlist();
            ga.decoding();
            ga.cfitness();           //计算适应度，并赋值给fitness[]
            //计算一次最小值
            Object[] objects = ga.best_value();
            best_solution[i] = Double.parseDouble(objects[1].toString());
            ga.selection();           //genetic_population_1利用原值并产生新值
            ga.crosssover();
            ga.change();
            ga.elitiststrategy();     //精英策略


            //算出来的是的适应度

            minnum[i]=1030-best_solution[i];        //f2



            ga.setIteration(i+1);
            ga.setMinnum2(minnum[i]);
            gaService.insertGa(ga);
        }


        List<GA> pageInfo = gaService.selectGa();
        return  pageInfo;
    }
}
