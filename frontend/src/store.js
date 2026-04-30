import { create } from 'zustand';

const useStore = create((set) => ({
  data: { sellers: [], customers: [], products: [], warehouses: [] },
  cart: [],
  calculation: null,
  history: [],
  form: {
    sellerId: '',
    customerId: '',
    productId: '',
    quantity: 1,
    deliverySpeed: 'standard'
  },
  
  setData: (data) => set({ data }),
  setForm: (form) => set((state) => ({ form: { ...state.form, ...form } })),
  
  addToCart: () => set((state) => {
    const product = state.data.products.find(p => p.id === state.form.productId);
    if (!product) return state;
    
    const existing = state.cart.find(item => item.productId === state.form.productId);
    if (existing) {
      return {
        cart: state.cart.map(item => 
          item.productId === state.form.productId 
            ? { ...item, quantity: item.quantity + state.form.quantity } 
            : item
        )
      };
    }
    return { cart: [...state.cart, { productId: state.form.productId, quantity: state.form.quantity, product }] };
  }),
  
  updateQuantity: (productId, delta) => set((state) => ({
    cart: state.cart.map(item => 
      item.productId === productId 
        ? { ...item, quantity: Math.max(1, item.quantity + delta) } 
        : item
    )
  })),
  
  removeFromCart: (productId) => set((state) => ({
    cart: state.cart.filter(item => item.productId !== productId)
  })),
  
  setCalculation: (calculation) => set({ calculation }),
  setHistory: (history) => set({ history }),
  
  resetOrder: () => set({ cart: [], calculation: null })
}));

export default useStore;
