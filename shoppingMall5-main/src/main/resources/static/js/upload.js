
// axis 처리를 위한 준비
// 파일 업로드 함수
async function uploadToServer (formObj)  {

    console.log("upload to server...")
    console.log(formObj)

    const response = await axios({
        method: 'post',
        url: '/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    })
    return response.data
}

// 파일 삭제 함수
async function removeFileToServer(uuid, fileName) {
    alert(`/remove/${uuid}_${fileName}`)
    const response = await axios.delete(`/remove/${uuid}_${fileName}`)
    return response.data
}