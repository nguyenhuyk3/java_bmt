package com.bmt.java_bmt.services;

import java.util.concurrent.TimeUnit;

/**
 * Interface định nghĩa các thao tác cơ bản với Redis
 *
 * @param <K> Kiểu dữ liệu của key
 * @param <V> Kiểu dữ liệu của value
 */
public interface IRedis<K, V> {
    /**
     * Kiểm tra xem key có tồn tại trong Redis hay không
     *
     * @param key Key cần kiểm tra
     * @return true nếu key tồn tại, false nếu không tồn tại
     */
    Boolean existsKey(K key);

    /**
     * Lưu dữ liệu vào Redis
     *
     * @param key Key để lưu trữ
     * @param value Giá trị cần lưu trữ
     */
    void save(K key, V value);

    /**
     * Lưu dữ liệu vào Redis với thời gian hết hạn
     *
     * @param key Key để lưu trữ
     * @param value Giá trị cần lưu trữ
     * @param timeout Thời gian hết hạn
     * @param timeUnit Đơn vị thời gian
     */
    void save(K key, V value, long timeout, TimeUnit timeUnit);

    /**
     * Xóa dữ liệu khỏi Redis
     *
     * @param key Key cần xóa
     * @return true nếu xóa thành công, false nếu key không tồn tại
     */
    Boolean delete(K key);

    /**
     * Xóa nhiều key cùng lúc
     *
     * @param keys Danh sách các key cần xóa
     * @return Số lượng key đã được xóa thành công
     */
    Long delete(K... keys);

    /**
     * Lấy dữ liệu từ Redis
     *
     * @param key Key cần lấy dữ liệu
     * @return Giá trị tương ứng với key, null nếu key không tồn tại
     */
    V get(K key);

    /**
     * Lấy thời gian sống còn lại của key (TTL - Time To Live)
     *
     * @param key Key cần kiểm tra TTL
     * @return Thời gian sống còn lại tính bằng giây
     *         -1 nếu key tồn tại nhưng không có thời gian hết hạn
     *         -2 nếu key không tồn tại
     */
    Long getTTL(K key);

    /**
     * Lấy thời gian sống còn lại của key với đơn vị thời gian chỉ định
     *
     * @param key Key cần kiểm tra TTL
     * @param timeUnit Đơn vị thời gian muốn trả về
     * @return Thời gian sống còn lại theo đơn vị chỉ định
     *         -1 nếu key tồn tại nhưng không có thời gian hết hạn
     *         -2 nếu key không tồn tại
     */
    Long getTTL(K key, TimeUnit timeUnit);
}
