export default {
    state: {
        isCollapse: false,
        selectedMenu: '',

    },
    mutations: {
        collapseMenu(state) {
            state.isCollapse = !state.isCollapse
        },
    }
}