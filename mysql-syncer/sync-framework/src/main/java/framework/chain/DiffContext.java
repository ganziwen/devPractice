package framework.chain;

import common.model.ConnectInfo;
import framework.enums.DiffType;
import lombok.Builder;
import lombok.Data;

/**
 * @author Ganziwen
 * @version 1.0
 * @ClassName DiffContext
 * @Description
 * @date 2021/10/6 14:06
 */
@Data
@Builder
public class DiffContext {
    DiffType diffType;
    private ConnectInfo srcConnectInfo;
    private ConnectInfo dstConnectInfo;
}
