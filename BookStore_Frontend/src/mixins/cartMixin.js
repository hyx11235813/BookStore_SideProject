import {throttle} from 'lodash';
import {getBooksById} from '@/api/book';
import {getUserCartInfo, updateCartQuantity} from '@/api/cart';
import {guestCart} from '@/utils/storage';
import {slugify} from '@/utils/format';

const HTTP_BAD_REQUEST = 400;
const HTTP_OK = 200;

/**
 * 購物車頁面（大、小螢幕版本）共用的邏輯。
 * 登入的使用者資料存在資料庫；未登入時存在 localStorage（guestCart）。
 */
export default {

    data() {
        return {
            products: [],
            selectedAll: false,
            showInfo: false,
            message: ''
        };
    },
    computed: {
        totalPrice() {
            return this.products
                .filter(product => product.selected)
                .reduce((sum, product) => sum + product.price * product.orderQuantity, 0);
        },
        selectedProductCount() {
            return this.products.filter(product => product.selected).length;
        },
        isSelectedAllProduct() {
            if (this.products.length === 0) return;
            return this.products.every(product => product.selected);
        }

    },
    watch: {
        isSelectedAllProduct() {
            this.selectedAll = this.isSelectedAllProduct;
        }

    },
    methods: {
        async increaseQuantity(index) {
            const {id, orderQuantity, bookQuantity} = this.products[index];
            const userId = this.getUserId();

            if (userId) {
                const {code} = await updateCartQuantity({bookId: id, modifyType: 'ADD'});
                if (code !== HTTP_OK) {
                    if (code === HTTP_BAD_REQUEST && orderQuantity + 1 > bookQuantity) {
                        this.showMessage('庫存不足！');
                    }
                    return;
                }
            } else {
                if (orderQuantity + 1 > bookQuantity) {
                    this.showMessage('庫存不足！');
                    return;
                }
                guestCart.add(id);
            }
            this.products[index].orderQuantity += 1;
            this.setCartProductQuantity();
        },
        decreaseQuantityThrottle: throttle(function (index) {
            this.decreaseQuantity(index);
        }, 500),
        async decreaseQuantity(index) {
            const {id: productId, orderQuantity} = this.products[index];
            const userId = this.getUserId();

            if (userId) {
                const {code} = await updateCartQuantity({bookId: productId, modifyType: 'SUBTRACT'});
                if (code === HTTP_OK) {
                    this.updateProductsModel(orderQuantity, index);
                    await this.updateUserCartQuantity(userId);
                }
                return;
            }

            const localCartData = guestCart.get();
            if (!orderQuantity) {
                delete localCartData[productId];
            } else {
                localCartData[productId] -= 1;
            }
            this.updateProductsModel(orderQuantity, index);
            guestCart.set(localCartData);
            this.setCartProductQuantity();
        },
        updateProductsModel(orderQuantity, index) {
            if (orderQuantity > 0) {
                this.products[index].orderQuantity = Number(this.products[index].orderQuantity) - 1;
            } else {
                this.products.splice(index, 1);
            }
        },
        showMessage(msg) {
            this.showInfo = true;
            this.message = msg;
            setTimeout(() => {
                this.showInfo = false;
            }, 2300);
        },
        async updateUserCartQuantity(userId) {
            try {
                const userCartProductQuantity = await getUserCartInfo(userId).then(res => res.data?.length || 0);
                this.$store.commit('setCartProductQuantity', userCartProductQuantity);
            } catch (error) {
                console.warn(error);
            }
        },
        toggleSelectAll() {
            for (const product of this.products) {
                product.selected = this.selectedAll;
            }
        },
        generateBookUrl(book) {
            if (book.bookName === undefined) return;
            this.$router.push(`/product/detail/${slugify(book.bookName)}/${book.isbn}`);
        },
        setCartProductQuantity() {
            const userCartQuantity = this.products.length;
            this.$store.commit('setCartProductQuantity', userCartQuantity > 0 ? userCartQuantity : guestCart.count());
        },
        async fetchCartItem() {
            try {
                const cart = guestCart.get();
                const localStorageProductIds = Object.keys(cart);
                const userId = this.getUserId();
                if (userId) {
                    const resp = await getUserCartInfo(userId);
                    this.products = this.formatUserCart(resp.data);
                } else if (localStorageProductIds.length > 0) {
                    const {data} = await getBooksById({ids: localStorageProductIds});
                    this.products = this.formatLocalCartProducts(data, cart);
                } else {
                    this.products = [];
                    this.selectedAll = false;
                }
            } catch (error) {
                console.error(error);
            }
        },
        formatUserCart(bookArray) {
            return bookArray.map(book =>
                this.formatBook(book, {
                    orderQuantity: book.orderQuantity
                })
            );
        },
        formatBook(book, extra = {}) {
            return {
                id: book.id,
                bookName: book.bookName,
                author: book.author,
                price: book.price,
                bookCoverByte: book.bookCoverByte,
                bookQuantity: book.quantity,
                selected: false,
                ...extra
            };
        },
        formatLocalCartProducts(booksArray, cart) {
            return booksArray.map(book => {
                const maxQuantity = book.quantity;
                const cartQuantity = cart[book.id];
                return this.formatBook(book, {
                    orderQuantity: maxQuantity === 0 ? 0 : Math.min(cartQuantity, maxQuantity)
                });
            });
        },
        getUserId() {
            return this.$store.state.userData.user.id;
        }
    },
    mounted() {
        this.fetchCartItem();
    }

};
