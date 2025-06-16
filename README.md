# 前端

| 接口             | 请求方式 | 是否需认证 | 关键字段                    | 断言逻辑           | 预期响应码 | 说明     |
| -------------- | ---- | ----- | ----------------------- | -------------- | ----- | ------ |
| /user/register | POST | ❌     | username/password/phone | msg 包含 "注册成功"  | 200   | 测试正常注册 |
| /user/register | POST | ❌     | 重复用户名                   | msg 包含 "已存在"   | 200   | 测试注册失败 |
| /user/login    | POST | ❌     | 正确用户名密码                 | data.token 不为空 | 200   | 登录成功   |
| /user/login    | POST | ❌     | 错误密码                    | msg="用户名或密码错误" | 200   | 登录失败   |
| /user/info     | POST | ✅     | header: Authorization   | 返回用户信息         | 200   | 获取当前用户 |
| /user/info     | POST | ✅     | 无Token                  | msg="用户未登录"    | 200   | 失败逻辑测试 |
| /user/all      | GET  | ✅     | 管理员身份Token              | 返回用户列表         | 200   | 获取所有用户 |
| /user/update   | PUT  | ✅     | 正确原密码与新数据               | msg="更新成功"     | 200   | 成功修改   |
| /user/update   | PUT  | ✅     | 原密码错误                   | msg="当前密码错误"   | 200   | 更新失败   |



| 接口路径                                    | 方法         | 权限要求    | 请求参数说明                              | 预期响应                      | 实测结果            | 是否通过            | 备注                         |
| --------------------------------------- | ---------- | ------- | ----------------------------------- | ------------------------- | --------------- | --------------- | -------------------------- |
| `/product/{id}`                         | GET        | 无       | 路径参数：商品 ID                          | 成功返回商品详情                  | ✅ 正确返回商品数据或错误信息 | ✅               | 商品不存在时返回“未找到该商品”           |
| `/product/list`                         | GET        | 无       | 无                                   | 返回所有商品列表                  | ✅ 返回商品列表数组      | ✅               | 数据正常展示                     |
| `/product/search`                       | GET        | 无       | name：商品名称关键字                        | 模糊匹配商品并返回列表               | ✅ 匹配成功或提示未找到    | ✅               | 中文模糊匹配支持正常                 |
| `/product/update`                       | PUT        | PRIMARY | JSON：Product 对象                     | 更新商品信息和 Redis 中缓存的图片地址    | ✅ 更新成功/失败原因     | ✅               | 图片更新依赖 Redis 中缓存值          |
| `/product/delete/{id}`                  | DELETE     | PRIMARY | 路径参数：商品 ID                          | 删除指定商品                    | ✅ 删除成功提示        | ✅               | 不存在 ID 也返回成功，建议加校验         |
| `/product/upimage`                      | POST       | PRIMARY | `file`: 图片文件（必填）<br>`id`: 商品 ID（可选） | 上传图片并返回访问地址               | ✅ 返回图片 URL      | ✅               | 如果未传 id，会先缓存图片 URL 到 Redis |
| `/product/add`                          | POST       | PRIMARY | JSON：Product 对象                     | 添加新商品并尝试绑定 Redis 中的图片 URL | ✅ 返回“新增成功”      | ✅               | Redis 有图片则绑定到数据库           |
| `/product/update` 后接 `/product/upimage` | PUT + POST | PRIMARY | 先上传图片再更新商品                          | 图片与商品正确绑定                 | ✅               | 需注意操作顺序：先上传图再更新 |                            |



| 序号     | 接口路径                      | 方法   | 权限要求    | 请求参数说明                          | 预期响应           | 实测结果           | 是否通过 | 备注               |
| ------ | ------------------------- | ---- | ------- | ------------------------------- | -------------- | -------------- | ---- | ---------------- |
| 1️⃣    | `/order/confirm/cache`    | POST | PRIMARY | body: Order JSON                | 缓存确认信息到 Redis  | ✅ 返回“确认信息已缓存”  | ✅    | 必须传完整 Order 数据结构 |
| 2️⃣    | `/order/confirm/get`      | GET  | PRIMARY | 无                               | 从 Redis 获取确认订单 | ✅ 返回 Order 对象  | ✅    | Redis 不存在则提示     |
| 3️⃣    | `/order/create`           | POST | PRIMARY | 无                               | 读取 Redis 创建订单  | ✅ 创建成功并清除缓存    | ✅    | 与确认缓存联动          |
| 4️⃣    | `/order/detail`           | POST | PRIMARY | JSON: `{ "id": 订单ID }`          | 获取订单详情         | ✅ 返回订单信息或提示无权限 | ✅    | 权限校验通过           |
| 5️⃣    | `/order/list`             | POST | PRIMARY | 无                               | 当前用户的全部订单列表    | ✅ 返回订单数组       | ✅    | 与当前登录用户相关联       |
| 6️⃣    | `/order/search`           | GET  | NONE    | keyword: 商品关键词                  | 模糊查询含商品名的订单    | ✅ 返回匹配的订单      | ✅    | 无需登录             |
| 7️⃣    | `/order/update/client`    | POST | PRIMARY | orderId, status                 | 客户更新订单状态       | ✅ 状态更新成功       | ✅    | 如取消订单            |
| 8️⃣    | `/order/update/delivery`  | POST | NONE    | orderId, status, deliveryId     | 配送员更新状态        | ✅ 状态更新成功       | ✅    | 示例：配送中、已完成       |
| 9️⃣    | `/order/update/admin`     | POST | NONE    | orderId, status, deliveryId     | 管理员后台更新状态      | ✅ 状态更新成功       | ✅    | 管理权限后续可加强验证      |
| 🔟     | `/order/all`              | GET  | PRIMARY | 无                               | 获取所有订单（管理员用）   | ✅ 返回全订单列表      | ✅    | 建议添加 admin 角色判断  |
| 1️⃣1️⃣ | `/order/admin/statistics` | GET  | NONE    | 无                               | 获取订单统计信息       | ✅ 返回统计 Map 数据  | ✅    | 示例：总数、销售额等       |
| 1️⃣2️⃣ | `/order/create`（注释版本）     | POST | PRIMARY | JSON: Order                     | 直接提交订单创建       | ✅ 可选使用         | ✅    | 当前逻辑切换为缓存版       |
| 1️⃣3️⃣ | `/order/update/...` 三个接口  | POST | 权限不同    | orderId + status (+deliveryId)  | 多角色状态更新        | ✅ 各自成功         | ✅    | 建议前端分类调用         |
| 1️⃣4️⃣ | Redis Key                 | -    | -       | key: `order:confirm:{username}` | 用于状态缓存中间态      | ✅ 正常存取         | ✅    | Redis TTL 可添加    |
| 1️⃣5️⃣ | 错误处理                      | -    | -       | -                               | 返回统一 Result 格式 | ✅ message 统一返回 | ✅    | 所有异常走统一格式        |

