package shipsensor.controller.ship;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.Route;
import shipsensor.service.RouteService;


@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {


    private final RouteService routeService;



    /**
     * 分页查询航线
     */
    @GetMapping("/page")
    public IPage<Route> page(

            @RequestParam(defaultValue = "1")
            int page,


            @RequestParam(defaultValue = "10")
            int count,


            @RequestParam(required = false)
            String keyword,


            @RequestParam(required = false)
            Long startPortId,


            @RequestParam(required = false)
            Long endPortId,


            @RequestParam(required = false)
            String sortField,


            @RequestParam(required = false)
            String sortOrder

    ){


        if(count > 100){

            count = 100;

        }


        LambdaQueryWrapper<Route> wrapper =
                new LambdaQueryWrapper<>();



        /**
         * 航线名称搜索
         */
        if(keyword != null && !keyword.isBlank()){


            wrapper.like(
                    Route::getRouteName,
                    keyword
            );


        }



        /**
         * 起始港口过滤
         */
        if(startPortId != null){

            wrapper.eq(
                    Route::getStartPortId,
                    startPortId
            );

        }



        /**
         * 目的港过滤
         */
        if(endPortId != null){

            wrapper.eq(
                    Route::getEndPortId,
                    endPortId
            );

        }



        applySort(
                wrapper,
                sortField,
                sortOrder
        );



        return routeService.page(

                new Page<>(page,count),

                wrapper

        );

    }





    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Route get(
            @PathVariable Long id
    ){

        return routeService.getById(id);

    }





    /**
     * 新增
     */
    @PostMapping
    public boolean save(
            @RequestBody Route route
    ){

        return routeService.save(route);

    }





    /**
     * 修改
     */
    @PutMapping
    public boolean update(
            @RequestBody Route route
    ){

        return routeService.updateById(route);

    }





    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public boolean delete(
            @PathVariable Long id
    ){

        return routeService.removeById(id);

    }





    /**
     * 排序
     */
    private void applySort(

            LambdaQueryWrapper<Route> wrapper,

            String sortField,

            String sortOrder

    ){


        if(sortField == null || sortField.isBlank()){

            return;

        }



        boolean asc =
                "asc".equalsIgnoreCase(sortOrder);



        switch(sortField){


            case "id":

                wrapper.orderBy(
                        true,
                        asc,
                        Route::getId
                );

                break;



            case "routeName":

                wrapper.orderBy(
                        true,
                        asc,
                        Route::getRouteName
                );

                break;



            case "distanceNm":

                wrapper.orderBy(
                        true,
                        asc,
                        Route::getDistanceNm
                );

                break;



        }


    }


}