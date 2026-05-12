import {getBooksById} from '@/api/book';
import {getUserCartInfo, updateCartQuantity} from '/src/api/cart';
import {throttle} from 'lodash';

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
            let product = this.products[index];
            const {id, orderQuantity, bookQuantity} = product;
            const userId = this.getUserId();
            const localCartData = this.getLocalCart();

            if (userId) {
                let payload = {
                    'userId': userId,
                    'bookId': id,
                    'quantity': 1,
                    'modifyType': 'ADD'
                };
                const {code} = await updateCartQuantity(payload);
                if (code === 400) {
                    if (orderQuantity + 1 > bookQuantity) {
                        this.showMessage('庫存不足！');
                        return;
                    }
                    return;
                } else {
                    this.products[index].orderQuantity += 1;
                }
            } else {
                if (orderQuantity + 1 > bookQuantity) {
                    this.showMessage('庫存不足！');
                    return;
                }
                localCartData[id] += 1;
                localStorage.setItem('cart', JSON.stringify(localCartData));
                this.products[index].orderQuantity += 1;
            }
            this.setCartProductQuantity();
        },
        decreaseQuantityThrottle: throttle(function (index) {
            this.decreaseQuantity(index);
        }, 500),
        async decreaseQuantity(index) {
            const orderQuantity = this.products[index].orderQuantity;
            const productId = this.products[index].id;
            const localCartData = this.getLocalCart();
            const userId = this.getUserId();
            if (userId) {
                const payload = {
                    'userId': userId,
                    'bookId': productId,
                    'quantity': 1,
                    'modifyType': 'SUBTRACT'
                };
                const {code} = await updateCartQuantity(payload);
                if (code === 200) {
                    this.updateProductsModel(orderQuantity, index);
                    await this.updateUserCartQuantity(userId);
                    return;
                }
                return;
            } else {
                if (!orderQuantity) {
                    delete localCartData[productId];
                } else {
                    localCartData[productId] -= 1;
                }
                this.updateProductsModel(orderQuantity, index);
                localStorage.setItem('cart', JSON.stringify(localCartData));
            }
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
            const slug = this.slugify(book.bookName);
            const url = `/product/detail/${slug}/${book.isbn}`;
            this.$router.push(url);
        },
        slugify(title) {
            return title
                .toLowerCase()
                .replace(/ /g, '-');
        },
        setCartProductQuantity() {
            const userCartQuantity = this.products.length;
            if (userCartQuantity > 0) {
                this.$store.commit('setCartProductQuantity', userCartQuantity);
            } else {
                const localCartQuantity = Object.keys(JSON.parse(localStorage.getItem('cart') || '{}')).length;
                this.$store.commit('setCartProductQuantity', localCartQuantity);
            }
        },
        async fetchCartItem() {
            try {
                const cart = this.getLocalCart();
                const localStorageProductIds = Object.keys(cart);
                const userId = this.$store.state.userData.user.id || 0;
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
        getLocalCart() {
            return JSON.parse(localStorage.getItem('cart') || '{}');
        },
        getUserId() {
            return this.$store.state.userData.user.id;
        }
    }
    ,
    mounted() {
        this.fetchCartItem();
    }

};
