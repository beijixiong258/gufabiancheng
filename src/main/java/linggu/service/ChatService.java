package linggu.service;

import linggu.dto.DuihuaDTO;
import linggu.vo.DuihuaVO;

public interface ChatService {
    boolean lianjie();
    DuihuaVO duihua(String yonghuId, DuihuaDTO duihuaDTO);
}
