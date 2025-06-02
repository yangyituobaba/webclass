import { defineStore } from 'pinia';
import useUserStore from "@/store/modules/userStore.js";

export const useCartStore = defineStore('cart', {
    state: () => ({
        items: [],  // { productId, name, price, quantity }
    }),
    actions: {
        addItem(product) {
            if (!product || !product.productId) {
                console.warn("无效的商品数据", product);
                return;
            }
            const existing = this.items.find(i => i.productId === product.productId);
            if (existing) {
                existing.quantity += product.quantity || 1;
            } else {
                this.items.push({
                    productId: product.productId,
                    name: product.name,
                    price: product.price,
                    quantity: product.quantity || 1
                });
            }
        }
        ,
        clearCart() {
            this.items = [];
        }
    },
    getters: {
        totalPrice: (state) => {
            return state.items.reduce((sum, i) => sum + i.price * i.quantity, 0);
        }
    }
});
export default useCartStore