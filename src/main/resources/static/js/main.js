window.addEventListener('load', () => {
    const videoList = document.querySelector('#videoList');

    /**
     * {
     *      data : [
     *          {
     *              videoId : 'ASDFAEFsdfaeFASEF',
     *              title : '어쩌구 저쩌구'
     *          },
     *          ...
     *      ]
     * }
     */
    fetchGet('/api/v1/videos', (json) => {

        let list = json;
        list.forEach(data => {
            let videoLi = generateVideoLi(data.videoId, data.title);
            videoList.appendChild(videoLi);
            addEvent(videoLi);
        });
    });
})

function addEvent(videoLi) {
    videoLi.addEventListener('click', () => {
        let id = videoLi.getAttribute('aria-id');
        window.location.href = '/video/' + id;
    });
}

function fetchGet(url, callback) {
    fetch(url)
        .then(res => res.json())
        .then(data => callback(data));
}

function generateVideoLi(videoId, title) {
    let li = document.createElement('li');
    li.className = 'videoLi';
    li.setAttribute('aria-id', videoId);

    let thumbnailDiv = document.createElement('div');
    thumbnailDiv.className = 'thumbnail';

    let img = document.createElement('img');
    img.src = '/thumbnail/' + videoId + '.jpg';
    img.alt = title;

    thumbnailDiv.appendChild(img);

    let description = document.createElement('div');
    description.className = 'description';

    let span = document.createElement('span');
    span.className = 'title';
    span.innerHTML = title;

    description.appendChild(span);

    li.appendChild(thumbnailDiv);
    li.appendChild(description);

    return li;
}