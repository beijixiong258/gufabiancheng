package linggu.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long page;
    private long size;
    private long pages;

    public static <T> PageResult<T> from(IPage<T> source) {
        return new PageResult<>(
                source.getRecords(),
                source.getTotal(),
                source.getCurrent(),
                source.getSize(),
                source.getPages()
        );
    }
}
