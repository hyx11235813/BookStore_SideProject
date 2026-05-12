import Vue from "vue";
import Vuex from "vuex"
import tab from "./modules/tab"
import userData from "./modules/userData";

Vue.use(Vuex)

export default new Vuex.Store({
    modules: {
        tab,
        userData
    }
})