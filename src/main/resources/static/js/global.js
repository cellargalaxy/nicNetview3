/**
 * Created by cellargalaxy on 17-11-11.
 */

var rootUrl = null;

function getRootUrl() {
    if (rootUrl == null) {
        rootUrl = $("#rootUrl").attr("value");
        if (rootUrl == '/') {
            rootUrl = '';
        }
    }
    return rootUrl;
}
/////////////////////////////////////
function createloginPersonnelFormVue() {
    var loginPersonnelForm = new Vue({
        el: '#loginPersonnelForm',
        data: {
            id: null, password: null
        },
        methods: {
            loginPersonnel: function () {
                loginPersonnel(this.id, this.password);
            }
        }
    });
    return loginPersonnelForm;
}
function loginPersonnel(id, password) {
    if (id == null || id == '') {
        alert("写个学号好让我知道你是谁吧");
        return;
    }
    if (password == null) {
        alert("密码都不输怎么登录");
        return;
    }
    $.ajax({
        url: getRootUrl() + '/login',
        type: 'post',
        data: {id: id, password: password},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",

        error: ajaxErrorDeal,
        success: function (data) {
            if (data.result) {
                location.href = getRootUrl() + '/page/listNetview';
            } else {
                alert(data.data);
            }
        }
    });
}
//////////////////////////////////////
function ajaxErrorDeal() {
    // alert("网络错误!");
}

