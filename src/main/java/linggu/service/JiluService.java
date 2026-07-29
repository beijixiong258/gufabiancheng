package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.dto.JiluXinjianDTO;
import linggu.entity.Jilu;

import java.util.List;

public interface JiluService extends IService<Jilu> {
    Jilu xinjian(String yonghuId,JiluXinjianDTO jiluXinjianDTO);
    boolean xiugai(Jilu jilu);
    Jilu chakan(String jiluId);
    boolean shanchu(String jiluId);
    boolean piliangShanchu(List<String> jiluIdList);//根据传入的ID列表批量删除。#讨论：这个操作要不要做成事务，一个失败全部回滚？考虑一种意外情况，删了n个成功m个，我想让前端能正确处理删掉的这m个
}
