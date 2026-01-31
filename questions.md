câu 1: 
Kho chỉ còn 1 sản phẩm iPhone 15. Có 2 khách hàng (A và B cùng bấm nút "Đặt hàng" tại chính xác cùng một thời điểm (mili-giây).
• Câu hỏi: "Làm sao nhóm đảm bảo không bị bán âm kho (oversell)? Nếu code của bạn hoạt động theo quy trình: B1: Lấy số lượng tồn kho (SELECT) -> B2: Kiểm tra nếu > 0 -> B3: Update trừ kho & Tạo đơn, thì khi 2 request cùng chạy B1 và thấy còn 1 cái, cả 2 đều qua B2, và cả 2 đều chạy B3. Kết quả kho sẽ là -1. Nhóm xử lý việc này thế nào?"

câu 2: 
Đề bài yêu cầu: "Nếu item nào stock không đủ → rollback toàn bộ."
• Câu hỏi: *"Giả sử tôi đặt đơn hàng gồm 3 món: A (còn hàng), B (hết hàng), C (còn hàng). Khi chạy Batch Insert order_items:
1. Món A trừ kho thành công.
2. Món B trừ kho thất bại (do hết hàng).
3. Lúc này, Transaction của nhóm sẽ xử lý sao? Nó có tự động rollback món A không hay nhóm phải code tay đoạn connection.rollback()? Nhóm có thể demo ngay trường hợp này không?