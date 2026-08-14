package shipsensor.controller.system;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.Port;
import shipsensor.service.PortService;


import java.util.List;



@RestController
@RequestMapping("/port")
@RequiredArgsConstructor
public class PortController {


    private final PortService portService;



    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Port getById(
            @PathVariable Long id
    ){

        return portService.getById(id);

    }





    /**
     * 查询全部港口
     */
    @GetMapping("/all")
    public List<Port> getAll(){

        return portService.list();

    }





    /**
     * 新增港口
     */
    @PostMapping
    public boolean save(
            @RequestBody Port port
    ){

        return portService.save(port);

    }





    /**
     * 批量新增
     */
    @PostMapping("/batch")
    public boolean saveBatch(
            @RequestBody List<Port> portList
    ){

        return portService.saveBatch(portList);

    }





    /**
     * 修改港口
     */
    @PutMapping
    public boolean update(
            @RequestBody Port port
    ){

        return portService.updateById(port);

    }





    /**
     * 删除港口
     */
    @DeleteMapping("/{id}")
    public boolean delete(
            @PathVariable Long id
    ){

        return portService.removeById(id);

    }





    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public boolean deleteBatch(
            @RequestBody List<Long> ids
    ){

        return portService.removeByIds(ids);

    }






    /**
     * 港口分页查询
     *
     * 支持：
     *
     * keyword:
     * 港口名称
     * 港口代码
     *
     * country:
     * 国家
     *
     * countryCode:
     * 国家编码
     *
     * portType:
     * 港口类型
     */
    @GetMapping("/page")
    public IPage<Port> getPage(


            @RequestParam(defaultValue = "1")
            int page,


            @RequestParam(defaultValue = "10")
            int count,


            @RequestParam(required = false)
            String keyword,


            @RequestParam(required = false)
            String country,


            @RequestParam(required = false)
            String countryCode,


            @RequestParam(required = false)
            String portType,


            @RequestParam(required = false)
            String sortField,


            @RequestParam(required = false)
            String sortOrder

    ){



        if(count > 100){

            count = 100;

        }



        LambdaQueryWrapper<Port> wrapper =
                new LambdaQueryWrapper<>();





        /**
         * 港口名称 / 港口代码搜索
         */
        if(keyword != null && !keyword.isBlank()){


            wrapper.and(w -> w

                    .like(
                            Port::getPortName,
                            keyword
                    )

                    .or()

                    .like(
                            Port::getPortCode,
                            keyword
                    )

            );

        }






        /**
         * 国家过滤
         */
        if(country != null && !country.isBlank()){


            wrapper.eq(
                    Port::getCountry,
                    country
            );

        }






        /**
         * 国家编码过滤
         */
        if(countryCode != null && !countryCode.isBlank()){


            wrapper.eq(
                    Port::getCountryCode,
                    countryCode
            );

        }






        /**
         * 港口类型过滤
         */
        if(portType != null && !portType.isBlank()){


            wrapper.eq(
                    Port::getPortType,
                    portType
            );

        }






        applySort(
                wrapper,
                sortField,
                sortOrder
        );






        return portService.page(

                new Page<>(page,count),

                wrapper

        );

    }







    /**
     * 排序
     */
    private void applySort(

            LambdaQueryWrapper<Port> wrapper,

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
                        Port::getId
                );

                break;





            case "portName":

                wrapper.orderBy(
                        true,
                        asc,
                        Port::getPortName
                );

                break;





            case "country":

                wrapper.orderBy(
                        true,
                        asc,
                        Port::getCountry
                );

                break;





            case "maxShipLength":

                wrapper.orderBy(
                        true,
                        asc,
                        Port::getMaxShipLength
                );

                break;





            case "maxDraft":

                wrapper.orderBy(
                        true,
                        asc,
                        Port::getMaxDraft
                );

                break;





            case "createTime":

                wrapper.orderBy(
                        true,
                        asc,
                        Port::getCreateTime
                );

                break;


        }


    }


}