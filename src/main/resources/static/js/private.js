/**
 * Created by cellargalaxy on 18-1-2.
 */

var token = null;


function getToken() {
    if (token == null) {
        token = $("#token").attr("value");
    }
    return token;
}
///////////////////////////////////////////////

function createAddEquipmentsFormVue() {
    var addEquipmentsFormVue = new Vue({
        el: '#addEquipmentsForm',
        data: {
            build: {}
        },
        methods: {
            uploadFile: function () {
                addEquipmentsForm(this.loadData);
            },
            loadData: function (data) {
                if (data.result) {
                    this.build = data.data;
                } else {
                    alert(data.data);
                }
            }
        }
    });
    return addEquipmentsFormVue;
}

function addEquipmentsForm(loadData) {
    var files = $('#equipmentFile').prop('files');
    if (files == null || files == '' || files.length == 0 || files[0] == null || files[0] == '') {
        alert('请选择文件');
        return;
    }
    var data = new FormData();
    data.append('file', files[0]);
    $.ajax({
        url: getRootUrl() + '/admin/uploadEquipmentFile',
        type: 'post',
        data: data,
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        cache: false,
        processData: false,
        contentType: false,

        error: ajaxErrorDeal,
        success: function (data) {
            loadData(data);
        }
    });
}
//////////////////////////////////////////////////

function createPaginationVue(flipPage) {
    var paginationVue = new Vue({
        el: '#pagination',
        data: {
            pages: [], localPage: 1, pageCount: 0
        },
        methods: {
            inquirePageCount: function (inquirePageCountFunc) {
                inquirePageCountFunc(this.loadData);
            },
            flipPage: function (page) {
                flipPage(page);
                this.localPage = page;
                var pageLen = 10;
                var start = this.localPage - pageLen;
                var end = this.localPage + pageLen;
                if (start < 1) {
                    start = 1;
                }
                if (end > this.pageCount) {
                    end = this.pageCount
                }
                this.pages = [];
                for (var i = start; i <= end; i++) {
                    this.pages.push(i);
                }
            },
            loadData: function (data) {
                if (data.result) {
                    this.pageCount = data.data;
                }
            }
        }
    });
    return paginationVue;
}

function inquireMalfunctionPageCount(loadData) {
    $.ajax({
        url: getRootUrl() + '/api/inquireMalfunctionPageCount',
        type: 'get',
        data: {token: getToken()},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        async: false,

        error: ajaxErrorDeal,
        success: function (data) {
            loadData(data);
        }
    });
}

/////////////////////////////////////////////////////

function createStackedNavigationVue() {
    var stackedNavigation = new Vue({
        el: '#stackedNavigation',
        data: {
            navigations: []
        },
        methods: {
            loadData: function (data) {
                if (data.result) {
                    this.navigations = data.data;
                }
            }
        }
    });
    return stackedNavigation;
}

//////////////////////////////////////////////////
function createListNetviewTableVue() {
    var listNetviewTableVue = new Vue({
        el: '#listNetviewTable',
        data: {
            builds: []
        },
        methods: {
            inquireNetview: function () {
                inquireNetview(this.loadData);
            },
            repeatNetview: function () {
                refreshNetview();
            },
            loadData: function (data) {
                if (data.result) {
                    this.builds = data.data;
                }
            }
        }
    });
    return listNetviewTableVue;
}

function inquireNetview(loadData) {
    $.ajax({
        url: getRootUrl() + '/api/inquireNetview',
        type: 'get',
        data: {token: getToken()},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",

        error: ajaxErrorDeal,
        success: function (data) {
            loadData(data);
        }
    });
}

function refreshNetview() {
    $.ajax({
        url: getRootUrl() + '/admin/refreshNetview',
        type: 'post',
        data: {},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",

        error: ajaxErrorDeal,
        success: function (data) {
            alert(data.data);
        }
    });
}
////////////////////////////////////////////////////////////////////////////////
function createListEquipmentTableVue() {
    var listEquipmentTableVue = new Vue({
        el: '#listEquipmentTable',
        data: {
            builds: [], rootUrl: getRootUrl()
        },
        methods: {
            inquireAllEquipment: function () {
                inquireAllEquipment(this.loadData);
            },
            removeEquipment: function (id) {
                removeEquipment(id);
                this.inquireAllEquipment();
            },
            loadData: function (data) {
                if (data.result) {
                    this.builds = data.data;
                }
            }
        }
    });
    return listEquipmentTableVue;
}

function inquireAllEquipment(loadData) {
    $.ajax({
        url: getRootUrl() + '/api/inquireAllEquipment',
        type: 'get',
        data: {token: getToken()},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",

        error: ajaxErrorDeal,
        success: function (data) {
            loadData(data);
        }
    });
}

function removeEquipment(id) {
    if (id == null || id == '') {
        alert("无效编号: " + id);
        return;
    }
    if (confirm("确认删除设备?: " + id)) {
        $.ajax({
            url: getRootUrl() + '/admin/removeEquipment',
            type: 'post',
            data: {id: id},
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            async: false,

            error: ajaxErrorDeal,
            success: function (data) {
                alert(data.data);
            }
        });
    }
}
//////////////////////////////////////////////////////////////////
function createListPlaceTableVue() {
    var listPlaceTableVue = new Vue({
        el: '#listPlaceTable',
        data: {
            places: [], area: '龙洞', build: null, floor: null, number: null
        },
        methods: {
            inquireAllPlace: function () {
                inquireAllPlace(this.loadData);
            },
            addPlace: function () {
                addPlace(this.area, this.build, this.floor, this.number);
                this.inquireAllPlace();
                this.area = '龙洞';
                this.build = null;
                this.floor = null;
                this.number = null;
            },
            removePlace: function (area, build, floor, number) {
                removePlace(area, build, floor, number);
                this.inquireAllPlace();
            },
            loadData: function (data) {
                if (data.result) {
                    this.places = data.data;
                }
            }
        }
    });
    return listPlaceTableVue;
}

function inquireAllPlace(loadData) {
    $.ajax({
        url: getRootUrl() + '/api/inquireAllPlace',
        type: 'get',
        data: {token: getToken()},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",

        error: ajaxErrorDeal,
        success: function (data) {
            loadData(data);
        }
    });
}

function addPlace(area, build, floor, number) {
    if (area == null || area == '') {
        alert("写个校区呢");
        return;
    }
    if (build == null || build == '') {
        alert("是哪个楼栋");
        return;
    }
    if (floor == null || floor == '') {
        alert(build + "的哪个楼层");
        return;
    }
    if (number == null || number == '') {
        alert("还差序号呢");
        return;
    }
    $.ajax({
        url: getRootUrl() + '/admin/addPlace',
        type: 'post',
        data: {area: area, build: build, floor: floor, number: number},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        async: false,

        error: ajaxErrorDeal,
        success: function (data) {
            alert(data.data);
        }
    });
}

function removePlace(area, build, floor, number) {
    if (confirm("确认删除地点?: " + area + '-' + build + '-' + floor + '-' + number)) {
        $.ajax({
            url: getRootUrl() + '/admin/removePlace',
            type: 'post',
            data: {area: area, build: build, floor: floor, number: number},
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            async: false,

            error: ajaxErrorDeal,
            success: function (data) {
                alert(data.data);
            }
        });
    }
}
///////////////////////////////////////////////////////////////////
function createListMalfunctionTableVue() {
    var listMalfunctionTableVue = new Vue({
        el: '#listMalfunctionTable',
        data: {
            malfunctions: []
        },
        methods: {
            inquireMalfunctions: function (page) {
                inquireMalfunctions(this.loadData, page);
            },
            removeMalfunction: function (area, build, floor, number, equipmentId, malfunctionDatetime) {
                removeMalfunction(area, build, floor, number, equipmentId, malfunctionDatetime);
                this.inquireMalfunctions(1);
            },
            loadData: function (data) {
                if (data.result) {
                    this.malfunctions = data.data;
                }
            }
        }
    });
    return listMalfunctionTableVue;
}

function inquireMalfunctions(loadData, page) {
    $.ajax({
        url: getRootUrl() + '/api/inquireMalfunctions',
        type: 'get',
        data: {"token": getToken(), page: page},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",

        error: ajaxErrorDeal,
        success: function (data) {
            loadData(data);
        }
    });
}

function removeMalfunction(area, build, floor, number, equipmentId, malfunctionDatetime) {
    if (area == null || area == '') {
        alert("写个校区呢");
        return;
    }
    if (build == null || build == '') {
        alert("是哪个楼栋");
        return;
    }
    if (floor == null || floor == '') {
        alert(build + "的哪个楼层");
        return;
    }
    if (number == null) {
        alert("还差序号呢");
        return;
    }
    if (equipmentId == null || equipmentId == '') {
        alert("设备的编号是什么");
        return;
    }
    if (malfunctionDatetime == null || malfunctionDatetime == '') {
        alert("还不知道故障发生的时间");
        return;
    }
    if (confirm("确认删除故障记录?: " + area + '-' + build + '-' + floor + '-' + number + '-' + equipmentId + '(' + malfunctionDatetime + ')')) {
        $.ajax({
            url: getRootUrl() + '/admin/removeMalfunction',
            type: 'post',
            data: {
                area: area,
                build: build,
                floor: floor,
                number: number,
                equipmentId: equipmentId,
                malfunctionDatetime: malfunctionDatetime
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            async: false,

            error: ajaxErrorDeal,
            success: function (data) {
                alert(data.data);
            }
        });
    }
}

////////////////////////////////////////////////////////////////////
function createEquipmentFormVue(func) {
    var equipmentFormVue = new Vue({
        el: '#equipmentForm',
        data: {
            equipment: {
                id: new Date().getTime(),
                model: null,
                name: null,
                buyDate: new Date().format("yyyy-MM-dd"),
                area: '龙洞',
                build: null,
                floor: null,
                number: null,
                ip: null,
                checkTimes: 3,
                isWarn: 1,
                remark: null,
                installDate: new Date().format("yyyy-MM-dd")
            }
        },
        methods: {
            inquireEquipmentById: function (id) {
                inquireEquipmentById(this.loadData, id);
            },
            post: function () {
                func(this.equipment.id, this.equipment.model, this.equipment.name, this.equipment.buyDate, this.equipment.area, this.equipment.build, this.equipment.floor, this.equipment.number, this.equipment.ip, this.equipment.checkTimes, this.equipment.isWarn, this.equipment.remark, this.equipment.installDate);
            },
            loadData: function (data) {
                if (data.result) {
                    this.equipment = data.data;
                } else {
                    this.equipment = {
                        id: '查无此机',
                        model: null,
                        name: null,
                        buyDate: null,
                        area: null,
                        build: null,
                        floor: null,
                        number: null,
                        ip: null,
                        checkTimes: 3,
                        isWarn: 1,
                        remark: null,
                        installDate: null
                    }
                }
            }
        }
    });
    return equipmentFormVue;
}

function inquireEquipmentById(loadData, id) {
    $.ajax({
        url: getRootUrl() + '/api/inquireEquipmentById',
        type: 'get',
        data: {token: getToken(), id: id},
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",

        error: ajaxErrorDeal,
        success: function (data) {
            loadData(data);
        }
    });
}

function addEquipment(id, model, name, buyDate, area, build, floor, number, ip, checkTimes, isWarn, remark, installDate) {
    if (id == null || id == '') {
        alert("给设备加个编号呗");
        return;
    }
    if (model == null || model == '') {
        alert("设备是啥机型");
        return;
    }
    if (buyDate == null || buyDate == '') {
        alert("这台机啥时候买的呢");
        return;
    }
    if (number == null | number == '') {
        number = 0;
    }
    if (checkTimes == null | checkTimes == '') {
        checkTimes = 0;
    }
    if (isWarn == null | isWarn == '') {
        isWarn = 0;
    }
    $.ajax({
        url: getRootUrl() + '/admin/addEquipment',
        type: 'post',
        data: {
            id: id,
            model: model,
            name: name,
            buyDate: buyDate,
            area: area,
            build: build,
            floor: floor,
            number: number,
            ip: ip,
            checkTimes: checkTimes,
            isWarn: isWarn,
            remark: remark,
            installDate: installDate
        },
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        async: false,

        error: ajaxErrorDeal,
        success: function (data) {
            alert(data.data);
            if (data.result) {
                location.href = getRootUrl() + '/page/listEquipment';
            }
        }
    });
}

function changeEquipment(id, model, name, buyDate, area, build, floor, number, ip, checkTimes, isWarn, remark, installDate) {
    if (id == null || id == '') {
        alert("给设备加个编号呗");
        return;
    }
    if (model == null || model == '') {
        alert("设备是啥机型");
        return;
    }
    if (buyDate == null || buyDate == '') {
        alert("这台机啥时候买的呢");
        return;
    }
    $.ajax({
        url: getRootUrl() + '/admin/changeEquipment',
        type: 'post',
        data: {
            id: id,
            model: model,
            name: name,
            buyDate: buyDate,
            area: area,
            build: build,
            floor: floor,
            number: number,
            ip: ip,
            checkTimes: checkTimes,
            isWarn: isWarn,
            remark: remark,
            installDate: installDate
        },
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        async: false,

        error: ajaxErrorDeal,
        success: function (data) {
            alert(data.data);
            if (data.result) {
                location.href = getRootUrl() + '/page/listEquipment';
            }
        }
    });
}
