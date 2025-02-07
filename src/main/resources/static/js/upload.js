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

    try {
        const response = await fetch(url, {
            method : 'POST',
            body: formData
        })
        if (response.ok) {
            const result = await response.text();
            alert('업로드 성공!');
        } else {
            alert('업로드 실패');
        }
    } catch (e) {
        console.error('Error:', e);
        alert('업로드 중 오류가 발생했습니다.');
    }

}