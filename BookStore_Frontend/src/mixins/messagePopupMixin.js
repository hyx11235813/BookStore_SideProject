export const messagePopupMixin = {
    data() {
        return {
            popupVisible: false, //顯示
            popupMessage: '',//顯示訊息
            popupType: ''//樣式類型
        };
    },
    methods: {
        showPopupMessage(message, type = 'info') {
            this.showMessage(message, type);
            setTimeout(() => {
                this.hideMessage();
            }, 2500);
        },
        showErrorMessage(message = '系統錯誤，請稍後再試！', type = 'warning') {
            this.showMessage(message, type);
            setTimeout(() => {
                this.hideMessage();
            }, 2500);
        },
        showMessage(message, type) {
            this.popupMessage = message;
            this.popupVisible = true;
            this.popupType = type;
        },
        hideMessage() {
            this.popupVisible = false;
        }
    }
};
