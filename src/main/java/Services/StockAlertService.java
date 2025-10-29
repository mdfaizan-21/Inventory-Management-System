package Services;

import DAO.Impl.ProductDAOImpl;
import DAO.ProductDAO;
import Models.Product;
import Models.User;
import util.EmailUtil;

import java.util.List;

public class StockAlertService {
    private final User user;

    public StockAlertService(User user) {
        this.user = user;
    }

    public void alertAdmin() {
        ProductDAO productDAO = new ProductDAOImpl();
        List<Product> products = productDAO.getAllProducts();

        // HTML email body with emojis and styling
        StringBuilder message = new StringBuilder("""
                <html>
                  <body style="font-family: Arial, sans-serif; background-color:#f8f9fa; color:#333; padding:20px;">
                    <h2 style="color:#e63946;">⚠️ Stock Alert Notification</h2>
                    <p>Dear <b>Admin</b>,</p>
                    <p>Some products in your inventory are running low 📉. Please review the details below:</p>
                    <table style="width:100%; border-collapse:collapse;">
                      <thead>
                        <tr style="background-color:#f1faee;">
                          <th style="border:1px solid #ccc; padding:8px;">🧾 Product Name</th>
                          <th style="border:1px solid #ccc; padding:8px;">📦 Current Qty</th>
                          <th style="border:1px solid #ccc; padding:8px;">🎯 Threshold</th>
                          <th style="border:1px solid #ccc; padding:8px;">💡 Recommended Add</th>
                        </tr>
                      </thead>
                      <tbody>
                """);

        int recommendedQty = 50;
        boolean hasLowStock = false;

        for (Product p : products) {
            if (p.getAvailableQty() < p.getThresholdLimit()) {
                hasLowStock = true;
                int qtyToAdd = recommendedQty - p.getAvailableQty();
                message.append("<tr>")
                        .append("<td style='border:1px solid #ccc; padding:8px;'>").append(p.getProductName()).append("</td>")
                        .append("<td style='border:1px solid #ccc; padding:8px; text-align:center;'>").append(p.getAvailableQty()).append("</td>")
                        .append("<td style='border:1px solid #ccc; padding:8px; text-align:center;'>").append(p.getThresholdLimit()).append("</td>")
                        .append("<td style='border:1px solid #ccc; padding:8px; text-align:center; color:#e63946;'><b>")
                        .append(qtyToAdd > 0 ? qtyToAdd : "⚠️ Refill Immediately")
                        .append("</b></td>")
                        .append("</tr>");
            }
        }

        message.append("""
                      </tbody>
                    </table>
                """);

        if (hasLowStock) {
            message.append("""
                    <p style="margin-top:20px;">Please restock these items soon to maintain smooth operations ✅</p>
                    <p style="font-size:0.9em; color:#6c757d;">📅 Checked automatically by the <b>Inventory Stock Alert System</b></p>
                  </body>
                </html>
                """);
        } else {
            message.append("""
                    <p style="margin-top:20px; color:green;">✅ All products are sufficiently stocked. Great job maintaining your inventory!</p>
                    <p style="font-size:0.9em; color:#6c757d;">📅 Checked automatically by the <b>Inventory Stock Alert System</b></p>
                  </body>
                </html>
                """);
        }

        EmailUtil.sendAlert(user.getEmail(), "⚠️ Stock Alert Notification", message.toString());
    }
}
