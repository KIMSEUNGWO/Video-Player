window.addEventListener('load', function() {
    const form = document.querySelector('#uploadForm');
    const videoInput = document.querySelector('#video');
    const thumbnailInput = document.querySelector('#thumbnail');
    const videoPreview = document.querySelector('#videoPreview');
    const thumbnailPreview = document.querySelector('#thumbnailPreview');
    const videoName = document.querySelector('#videoName');
    const thumbnailName = document.querySelector('#thumbnailName');
    const title = document.querySelector('#title');

    // 비디오 파일 선택 시 프리뷰
    videoInput.addEventListener('change', function (e) {
        const file = e.target.files[0];
        if (file) {
            videoName.textContent = file.name;
            const video = videoPreview.querySelector('video');
            video.src = URL.createObjectURL(file);
            videoPreview.style.display = 'block';
        }
    });

    // 썸네일 이미지 선택 시 프리뷰
    thumbnailInput.addEventListener('change',  (e) => {
        const file = e.target.files[0];
        if (file) {
            thumbnailName.textContent = file.name;
            const img = thumbnailPreview.querySelector('img');
            img.src = URL.createObjectURL(file);
            thumbnailPreview.style.display = 'block';
        }
    });

    // 폼 제출
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData();
        formData.append('video', videoInput.files[0]);
        formData.append('thumbnail', thumbnailInput.files[0]);
        formData.append('title', title.value);

        await fetchPost('/api/v1/upload', formData);
    });
});

async function fetchPost(url, formData) {

    await fetch(url, {
        method : 'POST',
        body: formData
    })
        .then(res => {
            if (!res.ok) {
                throw new Error("HTTP error! status : " + res.status);
            } else {
                return res.json();
            }
        })
        .then(json => {
            console.log(json);
            let key = json.statusKey;
            let message = json.message;
            alert(message);
            window.location.href = '/status?q=' + key;
        })
        .catch(error => {
            console.error('Error : ' + error);
            alert('업로드 중 오류가 발생했습니다.');
        })

}