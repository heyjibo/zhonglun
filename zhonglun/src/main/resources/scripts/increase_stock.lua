-- KEYS[1]: 库存的 key (e.g., 'product:stock:101')
-- ARGV[1]: 要增加的数量 (e.g., 2)

redis.call('INCRBY', KEYS[1], ARGV[1])
return 1
