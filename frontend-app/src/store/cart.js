import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

/**
 * 购物车状态管理 (Pinia Store)
 * 负责顾客端购物车的增、删、改、查、清空，并持久化到localStorage。
 */
export const useCartStore = defineStore('cart', () => {
    // --- State ---
    const items = ref(JSON.parse(localStorage.getItem('cartItems')) || []);

    // --- Getters (Computed Properties) ---
    // 计算购物车商品总数
    const totalItemsCount = computed(() => {
        return items.value.reduce((total, item) => total + item.quantity, 0);
    });

    // 计算购物车总价
    const totalPrice = computed(() => {
        const total = items.value.reduce((total, item) => total + item.price * item.quantity, 0);
        return parseFloat(total.toFixed(2)); // 保留两位小数
    });

    // --- Actions ---

    /**
     * 内部函数，用于将当前购物车状态持久化到localStorage
     */
    function _persistCart() {
        localStorage.setItem('cartItems', JSON.stringify(items.value));
    }

    /**
     * 添加商品到购物车
     * @param {object} product - 商品对象，至少包含 id, name, price
     */
    function addToCart(product) {
        const existingItem = items.value.find(item => item.id === product.id);

        if (existingItem) {
            // 如果商品已存在，增加数量
            existingItem.quantity++;
        } else {
            // 如果是新商品，添加到购物车
            items.value.push({ ...product, quantity: 1 });
        }
        _persistCart();
        ElMessage.success(`${product.name} 已加入购物车`);
    }

    /**
     * 从购物车移除商品
     * @param {number} productId
     */
    function removeFromCart(productId) {
        items.value = items.value.filter(item => item.id !== productId);
        _persistCart();
    }

    /**
     * 更新购物车中商品的数量
     * @param {number} productId
     * @param {number} quantity - 新的数量
     */
    function updateQuantity(productId, quantity) {
        const itemToUpdate = items.value.find(item => item.id === productId);
        if (itemToUpdate) {
            if (quantity > 0) {
                itemToUpdate.quantity = quantity;
            } else {
                // 如果数量小于等于0，则直接移除该商品
                removeFromCart(productId);
            }
            _persistCart();
        }
    }

    /**
     * 清空购物车
     */
    function clearCart() {
        items.value = [];
        _persistCart(); // 这会向localStorage写入一个空数组
    }

    /**
     * 获取用于创建订单的购物车项目列表
     * @returns {Array<{productId: number, quantity: number}>} - 符合后端CreateOrderRequest格式的数组
     */
    function getItemsForOrder() {
        return items.value.map(item => ({
            productId: item.id,
            quantity: item.quantity,
        }));
    }

    return {
        items,
        totalItemsCount,
        totalPrice,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        getItemsForOrder,
    };
});
