package shipsensor.simulator.initializer;

import com.alibaba.excel.EasyExcel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import shipsensor.entity.Port;
import shipsensor.inter.PortMapper;
import shipsensor.simulator.model.PortExcelDTO;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

@Component
@Order(1)
@Slf4j
public class PortInitializer {

    private final PortMapper portMapper;

    public PortInitializer(PortMapper portMapper) {
        this.portMapper = portMapper;
    }

    @PostConstruct
    public void init() {
        try {
            // 1. 检查是否已存在 Port 数据
            Long count = portMapper.selectCount(null);
            if (count != null && count > 0) {
                log.info("PORT表已有数据，跳过初始化");
                return;
            }

            // 2. 从 classpath 读取 ports.xlsx
            Resource resource = new ClassPathResource("data/ports.xlsx");
            if (!resource.exists()) {
                log.error("ports.xlsx 文件不存在，请检查 resources/data/ 目录");
                return;
            }

            InputStream inputStream = resource.getInputStream();

            // 3. 使用 EasyExcel 读取 Excel 数据
            List<PortExcelDTO> excelData = EasyExcel.read(inputStream)
                    .head(PortExcelDTO.class)
                    .sheet()
                    .doReadSync();

            // 4. 转换为 Port 实体类
            List<Port> ports = new ArrayList<>();
            for (PortExcelDTO dto : excelData) {
                if (dto.getPortName() == null || dto.getCountry() == null) {
                    continue;
                }

                Port port = new Port();
                port.setWpiId(dto.getWpiId());
                port.setPortName(dto.getPortName());
                port.setCountry(dto.getCountry());
                port.setCountryCode(dto.getCountryCode());
                port.setPortCode(dto.getPortCode());
                port.setPortType(dto.getPortType());
                port.setLatitude(dto.getLatitude());
                port.setLongitude(dto.getLongitude());
                port.setMaxShipLength(dto.getMaxShipLength());
                port.setMaxDraft(dto.getMaxDraft());

                ports.add(port);
            }

            // 5. 批量插入 Port 数据
            if (!ports.isEmpty()) {
                portMapper.insertBatch(ports);
                log.info("港口初始化完成: {} 条", ports.size());
            } else {
                log.warn("Excel 中无有效 Port 数据，跳过插入");
            }

        } catch (Exception e) {
            log.error("读取或插入 Port 数据失败", e);
        }
    }
}
