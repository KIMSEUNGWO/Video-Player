// window.addEventListener('load', () => {
//     const video = document.querySelector('#videoPlayer');
//     const progress = document.querySelector('.progress');
//     const timeDisplay = document.querySelector('.time-display');
//     const volumeProgress = document.querySelector('.volume-progress');
//     const qualityBtn = document.querySelector('.quality-btn');
//     let isDraggingVolume = false;
//     let hls;
//
//     const videoId = video.getAttribute('aria-id');
//
//     // HLS 설정
//     if (Hls.isSupported()) {
//         hls = new Hls({
//             debug: true,  // 디버깅을 위해 추가
//             levelLoadingTimeOut: 10000,  // 타임아웃 설정
//         });
//
//         const playlistUrl = `/videos/${videoId}/master.m3u8`;
//         console.log('Loading playlist:', playlistUrl);  // 디버깅용 로그
//         hls.loadSource(playlistUrl);
//         hls.attachMedia(video);
//
//         hls.on(Hls.Events.MANIFEST_PARSED, function(event, data) {
//             console.log('Available qualities:', data.levels);
//
//             // 현재 화질 레벨 설정
//             let currentLevel = hls.currentLevel;
//             updateQualityButton(currentLevel);
//
//             // 자동 화질 선택 모드 설정
//             hls.currentLevel = -1;
//         });
//
//         // 화질 변경 이벤트 처리
//         hls.on(Hls.Events.LEVEL_SWITCHED, function(event, data) {
//             updateQualityButton(data.level);
//         });
//     }
//
//     // 화질 선택 버튼 이벤트
//     const qualityOptions = document.querySelectorAll('.quality-options button');
//     qualityOptions.forEach(button => {
//         button.addEventListener('click', () => {
//             const quality = parseInt(button.getAttribute('data-quality'));
//             hls.currentLevel = quality;
//             updateQualityButton(quality);
//         });
//     });
//
//
//     // 재생/일시정지 토글
//     const playPauseBtn = document.querySelector('.play-pause');
//     playPauseBtn.addEventListener('click', function() {
//         if (video.paused) {
//             video.play();
//             playPauseBtn.innerHTML = getPauseSvg();
//         } else {
//             video.pause();
//             playPauseBtn.innerHTML = getPlaySvg();
//         }
//     });
//
//     // 타임라인 업데이트
//     video.addEventListener('timeupdate', function() {
//         const percentage = (video.currentTime / video.duration) * 100;
//         progress.style.width = percentage + '%';
//         timeDisplay.textContent = `${formatTime(video.currentTime)} / ${formatTime(video.duration)}`;
//     });
//
//     // 타임라인 클릭
//     const timeline = document.querySelector('.timeline');
//     timeline.addEventListener('click', function(e) {
//         const rect = timeline.getBoundingClientRect();
//         const pos = (e.clientX - rect.left) / rect.width;
//         video.currentTime = pos * video.duration;
//     });
//
//     // 볼륨 조절
//     const volumeSlider = document.querySelector('.volume-slider');
//     volumeSlider.addEventListener('click', function(e) {
//         const rect = volumeSlider.getBoundingClientRect();
//         const pos = (e.clientX - rect.left) / rect.width;
//         video.volume = pos;
//         volumeProgress.style.width = (pos * 100) + '%';
//         updateVolumeIcon(pos);
//     });
//
//     // 볼륨 음소거 토글
//     const volumeBtn = document.querySelector('.volume-btn');
//     volumeBtn.addEventListener('click', function() {
//         video.muted = !video.muted;
//         updateVolumeIcon(video.muted ? 0 : video.volume);
//     });
//
//     // 전체화면 토글
//     const fullscreenBtn = document.querySelector('.fullscreen');
//     fullscreenBtn.addEventListener('click', function() {
//         if (!document.fullscreenElement) {
//             video.parentElement.requestFullscreen();
//         } else {
//             document.exitFullscreen();
//         }
//     });
//
//     function handleVolumeChange(e) {
//         const rect = volumeSlider.getBoundingClientRect();
//         let pos = (e.clientX - rect.left) / rect.width;
//         pos = Math.max(0, Math.min(1, pos)); // Clamp between 0 and 1
//         video.volume = pos;
//         volumeProgress.style.width = (pos * 100) + '%';
//         updateVolumeIcon(pos);
//     }
//
//     // Volume slider mouse events
//     volumeSlider.addEventListener('mousedown', (e) => {
//         isDraggingVolume = true;
//         handleVolumeChange(e);
//     });
//
//     document.addEventListener('mousemove', (e) => {
//         if (isDraggingVolume) {
//             handleVolumeChange(e);
//         }
//     });
//
//     document.addEventListener('mouseup', () => {
//         isDraggingVolume = false;
//     });
//
//     // Volume button click (mute/unmute)
//     volumeBtn.addEventListener('click', function() {
//         video.muted = !video.muted;
//         updateVolumeIcon(video.muted ? 0 : video.volume);
//     });
//
//     // Initialize volume display
//     video.addEventListener('loadedmetadata', () => {
//         updateVolumeIcon(video.volume);
//     });
//
//     const qualityOptionsBox = document.querySelector('.quality-options');
//     qualityBtn.addEventListener('click', (e) => {
//         qualityOptionsBox.classList.toggle('disabled');
//     })
//     // 화질 버튼 텍스트 업데이트
//     function updateQualityButton(level) {
//         let qualityText = 'AUTO';
//         if (level === 0) qualityText = '360p';
//         else if (level === 1) qualityText = '480p';
//         else if (level === 2) qualityText = '720p';
//         else if (level === 3) qualityText = '1080p';
//         qualityBtn.textContent = qualityText;
//         qualityOptionsBox.classList.toggle('disabled');
//     }
//
//
//
//     // 페이지가 로드되면 바로 동영상을 재생
//     video.play();
//     playPauseBtn.innerHTML = getPauseSvg();
// });
//
// // 시간 포맷팅
// function formatTime(seconds) {
//     if (isNaN(seconds)) return '--:--';
//     const minutes = Math.floor(seconds / 60);
//     seconds = Math.floor(seconds % 60);
//     return `${minutes}:${seconds.toString().padStart(2, '0')}`;
// }
//
// // 볼륨 아이콘 업데이트
// function updateVolumeIcon(volume) {
//     const volumeBtn = document.querySelector('.volume-btn');
//     const volumeLevel = Math.floor(volume * 100);
//     if (volumeLevel === 0) {
//         volumeBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64l0 384c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352 64 352c-35.3 0-64-28.7-64-64l0-64c0-35.3 28.7-64 64-64l67.8 0L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM425 167l55 55 55-55c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9l-55 55 55 55c9.4 9.4 9.4 24.6 0 33.9s-24.6 9.4-33.9 0l-55-55-55 55c-9.4 9.4-24.6 9.4-33.9 0s-9.4-24.6 0-33.9l55-55-55-55c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0z"/></svg>';
//     } else if (volumeLevel <= 50) {
//         volumeBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64l0 384c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352 64 352c-35.3 0-64-28.7-64-64l0-64c0-35.3 28.7-64 64-64l67.8 0L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM412.6 181.5C434.1 199.1 448 225.9 448 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C393.1 284.4 400 271 400 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5z"/></svg>';
//     } else {
//         volumeBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M333.1 34.8C312.6 40 320 51.4 320 64l0 384c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352 64 352c-35.3 0-64-28.7-64-64l0-64c0-35.3 28.7-64 64-64l67.8 0L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zm172 72.2c43.2 35.2 70.9 88.9 70.9 149s-27.7 113.8-70.9 149c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C507.3 341.3 528 301.1 528 256s-20.7-85.3-53.2-111.8c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5zm-60.5 74.5C466.1 199.1 480 225.9 480 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C425.1 284.4 432 271 432 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5z"/></svg>';
//     }
// }
//
// function getPauseSvg() {
//     return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path d="M48 64C21.5 64 0 85.5 0 112L0 400c0 26.5 21.5 48 48 48l32 0c26.5 0 48-21.5 48-48l0-288c0-26.5-21.5-48-48-48L48 64zm192 0c-26.5 0-48 21.5-48 48l0 288c0 26.5 21.5 48 48 48l32 0c26.5 0 48-21.5 48-48l0-288c0-26.5-21.5-48-48-48l-32 0z"/></svg>';
// }
// function getPlaySvg() {
//     return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="M73 39c-14.8-9.1-33.4-9.4-48.5-.9S0 62.6 0 80L0 432c0 17.4 9.4 33.4 24.5 41.9s33.7 8.1 48.5-.9L361 297c14.3-8.7 23-24.2 23-41s-8.7-32.2-23-41L73 39z"/></svg>';
// }