// 创建Vue实例化对象
new Vue({
    //绑定节点
    el: "#textExtract",
    //定义变量
    data() {
        return{
            fileList: [],
            submitDisable: true,
            extractForm: {
                startingText: '',
                sortOrNot: false,
                sortOrder: '',
                sortPosition: '',
                startingPosition: '',
                endPosition: ''
            },
            options: [{
                value: '1',
                label: '截取文字首位'
            }, {
                value: '2',
                label: '截取文字末位'
            }, {
                value: '3',
                label: '截取文字某位'
            }],
        }
    },

    methods:{
        submitForm(){
            const _this = this;
            const formData = new FormData();
            for (let i = 0; i < _this.fileList.length; i++) {
                formData.append(`files`, _this.fileList[i].file);
            }
            formData.append("extractForm", JSON.stringify(_this.extractForm));
            axios({
                method: 'POST',
                url: getRootPath()+"/textExtractHandle",
                data: formData,
                timeout: 10000,
                //表明返回服务器返回的数据类型
                responseType: 'blob'
            }).then(function (res) {
                const data = res.data;
                const blob = new Blob([data], {type: 'application/octet-stream'});
                const url = URL.createObjectURL(blob);
                const exportLink = document.createElement('a');
                let fileName = res.headers["content-disposition"].split(";")[1].split("filename=")[1]
                exportLink.setAttribute("download",decodeURI(fileName));
                exportLink.href = url;
                document.body.appendChild(exportLink);
                exportLink.click();
                document.body.removeChild(exportLink);
            })
        },

        changFile(file, fileList) {
            const _this = this;
            _this.fileList.push({
                id: file.uid,
                name: file.name,
                file: file.raw
            })
            _this.submitDisable = false;
        },

        sortChange(){
          if (!this.extractForm.sortOrNot){
              this.extractForm.sortOrder = '';
              this.extractForm.sortPosition = '';
          }
        },

        //清空文件列表
        clearFileList (id) {
            let fileListIdx = this.fileList.findIndex(item => item.id === id);
            this.fileList.splice(fileListIdx, 1);
            if (this.fileList.length == 0){
                this.submitDisable = true;
            }
        },

        //清空显示列表
        clearShowList (uid) {
            let idx = this.$refs['uploadFile'].uploadFiles.findIndex(item => item.uid === uid);
            this.$refs.uploadFile.uploadFiles.splice(idx, 1);
        },

        handleRemove(file) {
            this.clearFileList(file.uid);
            this.clearShowList(file.uid);
        }
    },

    created(){},
    mounted(){}
})