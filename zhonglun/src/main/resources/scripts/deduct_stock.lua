-- KEYS[1]: 库存的 key (e.g., 'product:stock:101')
-- ARGV[1]: 要扣减的数量 (e.g., 2)

-- 获取当前库存
local current_stock = tonumber(redis.call('GET', KEYS[1]))

-- 如果库存存在且足够
if current_stock and current_stock >= tonumber(ARGV[1]) then
  -- 执行扣减并返回扣减后的库存
  local new_stock = redis.call('DECRBY', KEYS[1], ARGV[1])
  -- 返回 1 表示成功
  return 1
else
  -- 库存不足或key不存在，返回 0 表示失败
  return 0
end
