window.addEventListener('load', () => {

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
    fetchGet('/api/v1/videos');
})

function addEvent(videoLi) {
    videoLi.addEventListener('click', () => {
        let id = videoLi.getAttribute('aria-id');
        window.location.href = '/video/' + id;
    });
}

function fetchGet(url) {
    const videoList = document.querySelector('#videoList');

    fetch(url)
        .then(res => res.json())
        .then(json => {
            json.forEach(data => {
                let videoLi = generateVideoLi(data.videoId, data.thumbnail, data.title);
                videoList.appendChild(videoLi);
                addEvent(videoLi);
            });
        });
}

function generateVideoLi(videoId, thumbnail, title) {
    let li = document.createElement('li');
    li.className = 'videoLi';
    li.setAttribute('aria-id', videoId);

    let thumbnailDiv = generateThumbnailElement(thumbnail, title);
    li.appendChild(thumbnailDiv);

    let description = generateDescription(title);
    li.appendChild(description);

    return li;
}

function generateThumbnailElement(thumbnailId, title) {
    let thumbnailDiv = document.createElement('div');
    thumbnailDiv.className = 'thumbnail';

    let img = document.createElement('img');
    img.src = '/thumbnail/' + thumbnailId;
    img.alt = title;

    thumbnailDiv.appendChild(img);
    return thumbnailDiv;
}

function generateDescription(title) {
    let description = document.createElement('div');
    description.className = 'description';

    let span = document.createElement('span');
    span.className = 'title';
    span.innerHTML = title;

    description.appendChild(span);
    return description;
}