package top.pxczxn.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {

    private long total;
    private List<T> list;
    private long page;
    private long size;
}
