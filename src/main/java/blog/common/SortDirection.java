package blog.common;

import lombok.Getter;

@Getter
public enum SortDirection {
    ASC(1),   // 升序
    DESC(-1),  // 降序
    RANDOM(0);// 随机

    private final int value;

    SortDirection(int value) {
        this.value = value;
    }
}
