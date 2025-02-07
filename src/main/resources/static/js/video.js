const KEYS = {
    NEXT : 'ArrowRight',
    PREV : 'ArrowLeft',
    PLAY_PAUSE : 'Space',
    FULL_SCREEN : 'KeyF'
}
const DISABLED = 'disabled';

class Video {

    constructor(videoElement, playlistUrl) {
        if (!this.#isSupported()) return;
        this.video = videoElement;
        this.loadingSpinner = document.querySelector('.loading-spinner');

        this.#setHls(playlistUrl);
        this.controller = new Controller(this.hls, this.video, this.loadingSpinner);
        this.#addLoadingEvent();
    }

    #addLoadingEvent() {
        // 로딩 시작
        this.video.addEventListener('waiting', () => {
            this.loadingSpinner.classList.remove(DISABLED);
            this.controller.panel.classList.add('active');
        });

        // 로딩 완료
        this.video.addEventListener('canplay', () => {
            this.loadingSpinner.classList.add(DISABLED);
            this.controller.panel.classList.remove('active');
        });

    }
    #isSupported() {
        return Hls.isSupported();
    }
    #setHls(playlistUrl) {
        this.hls = new Hls({
            debug: true,  // 디버깅을 위해 추가
            levelLoadingTimeOut: 10000,  // 타임아웃 설정
        });

        this.hls.loadSource(playlistUrl);
        this.hls.attachMedia(this.video);
    }

    initialEventListener() {
        this.controller.initialDependenciesEventListener();
    }

    play() {
        this.controller.playPause.play();
    }

    pause() {
        this.controller.playPause.pause();
    }

}

class Controller {

    constructor(hls, video, loadingSpinner) {
        this.hls = hls;
        this.video = video;
        this.loadingSpinner = loadingSpinner;
    }

    setPanel(panelElement) {
        this.panel = panelElement;
    }
    setQuality(qualityBtnElement, qualityOptionsBox) {
        this.quality = new VideoQuality(qualityBtnElement, qualityOptionsBox);

        // 화질 최초 선택
        this.hls.on(Hls.Events.MANIFEST_PARSED, (event, data) => {
            console.log('Available qualities:', data.levels);

            // 사용 가능한 화질 옵션 생성
            this.quality.createQualityOptions(data.levels);
            this.quality.addEventChangeQuality(this.hls, this.playPause, this.loadingSpinner);

            // 현재 화질 레벨 설정
            let currentLevel = this.hls.currentLevel;
            this.quality.updateQualityButton(currentLevel, data.levels);

            // 자동 화질 선택 모드 설정
            this.hls.currentLevel = -1;
        });

        // 화질 변경 이벤트 처리
        this.hls.on(Hls.Events.LEVEL_SWITCHED, (event, data) => {
            this.quality.updateQualityButton(data.level);
        });

    }
    setVolume(volumeBtn, volumeSlider, volumeProgress) {
        this.volume = new VideoVolume(this.video, volumeBtn, volumeSlider, volumeProgress);
    }
    setPlayPauseBtn(playPauseBtnElement, playPauseEventElement) {
        this.playPause = new VideoPlayPause(this.video, this.panel, playPauseBtnElement, playPauseEventElement);
    }
    setFullScreen(fullscreenBtnElement) {
        this.fullScreen = new VideoFullScreen(this.video, fullscreenBtnElement);
    }
    setTimeline(timelineElement, progressElement, timeDisplay) {
        this.timeline = new VideoTimeline(this.video, timelineElement, progressElement, timeDisplay);
    }

    initialDependenciesEventListener() {
        this.timeline.setDraggingEvent(this.playPause);
    }

    setKeyShortCuts() {
        document.addEventListener('keydown', (e) => {
            // 입력 필드에 포커스가 있을 때는 단축키 비활성화
            if (document.activeElement.tagName === 'INPUT' ||
                document.activeElement.tagName === 'TEXTAREA') {
                return;
            }
            this.playPause.keyShortcuts(e,10, 10);
            this.fullScreen.keyShortcuts(e);
        })

    }
}

class VideoPlayPause {

    constructor(video, panel, playPauseBtnElement, playPauseEventElement) {
        this.video = video;
        this.panel = panel;
        this.playPauseBtn = playPauseBtnElement;
        this.playPauseEvent = playPauseEventElement;

        this.#addEvent();
    }

    #addEvent() {
        this.playPauseBtn.addEventListener('click', () => {
            this.#playPauseToggle();
        });

        this.panel.addEventListener('click', (e) => {
            if (e.target !== this.panel) return;
            this.#playPauseToggle();
            this.#showEventAnimation();
        })

        // 비디오 종료 시 reload 버튼 변경
        this.video.addEventListener('ended', () => {
            this.playPauseBtn.innerHTML = this.getReloadSvg();
        });
    }

    /**
     * @param e
     * @param increaseAmount : number n초 후로 비디오 타임라인 이동
     * @param decreaseAmount : number n초 전으로 비디오 타임라인 이동
     */
    keyShortcuts(e, increaseAmount, decreaseAmount) {
        if (e.code === KEYS.NEXT) {
            // 오른쪽 화살표 (n초 앞으로)
            this.video.currentTime = Math.min(this.video.duration, this.video.currentTime + increaseAmount);
        } else if (e.code === KEYS.PREV) {
            // 왼쪽 화살표 (n초 뒤로)
            this.video.currentTime = Math.max(0, this.video.currentTime - decreaseAmount);
        } else if (e.code === KEYS.PLAY_PAUSE) {
            // 시작, 일시정지
            this.#playPauseToggle();
        }
    }

    #playPauseToggle() {
        this.isPaused() ? this.play() : this.pause();
    }

    #showEventAnimation() {
        // 이전 타이머가 있다면 정리
        if (this.activeTimer) {
            clearTimeout(this.activeTimer);
            this.playPauseEvent.classList.remove('active');
            // 강제 리플로우 발생
            void this.playPauseEvent.offsetWidth;
        }

        this.playPauseEvent.classList.add('active');

        this.activeTimer = setTimeout(() => {
            this.playPauseEvent.classList.remove('active');
            this.activeTimer = null;
        }, 700);
    }

    play() {
        this.video.play();
        this.playPauseBtn.innerHTML = this.getPauseSvg();
        this.playPauseEvent.innerHTML = this.getPauseSvg();
    }

    pause() {
        this.video.pause();
        this.playPauseBtn.innerHTML = this.getPlaySvg();
        this.playPauseEvent.innerHTML = this.getPlaySvg();
    }

    isPaused() {
        return this.video.paused;
    }

    getPauseSvg() {
        return '<svg class="animate-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path d="M48 64C21.5 64 0 85.5 0 112L0 400c0 26.5 21.5 48 48 48l32 0c26.5 0 48-21.5 48-48l0-288c0-26.5-21.5-48-48-48L48 64zm192 0c-26.5 0-48 21.5-48 48l0 288c0 26.5 21.5 48 48 48l32 0c26.5 0 48-21.5 48-48l0-288c0-26.5-21.5-48-48-48l-32 0z"/></svg>';
    }
    getPlaySvg() {
        return '<svg class="animate-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="M73 39c-14.8-9.1-33.4-9.4-48.5-.9S0 62.6 0 80L0 432c0 17.4 9.4 33.4 24.5 41.9s33.7 8.1 48.5-.9L361 297c14.3-8.7 23-24.2 23-41s-8.7-32.2-23-41L73 39z"/></svg>';
    }
    getReloadSvg() {
        return '<svg class="animate-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M463.5 224H472c13.3 0 24-10.7 24-24V72c0-9.7-5.8-18.5-14.8-22.2s-19.3-1.7-26.2 5.2L413.4 96.6c-87.6-86.5-228.7-86.2-315.8 1c-87.5 87.5-87.5 229.3 0 316.8s229.3 87.5 316.8 0c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0c-62.5 62.5-163.8 62.5-226.3 0s-62.5-163.8 0-226.3c62.2-62.2 162.7-62.5 225.3-1L327 183c-6.9 6.9-8.9 17.2-5.2 26.2s12.5 14.8 22.2 14.8H463.5z"/></svg>';
    }

}
class VideoQuality {

    constructor(qualityBtnElement, qualityOptionsBox) {
        this.qualityBtn = qualityBtnElement;
        this.qualityOptionsBox = qualityOptionsBox;

        this.#addEvent();
    }

    createQualityOptions(levels) {
        this.qualityOptionsBox.innerHTML = '';

        // AUTO 옵션 추가
        const autoButton = document.createElement('button');
        autoButton.setAttribute('data-quality', '-1');
        autoButton.textContent = 'AUTO';
        this.qualityOptionsBox.appendChild(autoButton);

        // 높은 화질부터 추가
        for (let i = levels.length - 1; i >= 0; i--) {
            const level = levels[i];
            const resolution = level.height + 'p';
            const button = document.createElement('button');
            button.setAttribute('data-quality', i.toString());
            button.textContent = resolution;
            this.qualityOptionsBox.appendChild(button);
        }
    }

    // 화질 버튼 텍스트 업데이트
    updateQualityButton(level) {
        let qualityText = 'AUTO';

        if (level === 0) qualityText = '360p';
        else if (level === 1) qualityText = '480p';
        else if (level === 2) qualityText = '720p';
        else if (level === 3) qualityText = '1080p';

        this.qualityBtn.textContent = qualityText;
        this.qualityOptionsBox.classList.add(DISABLED);
    }

    #addEvent() {
        // 선택 후 박스 감춤 이벤트
        this.qualityBtn.addEventListener('click', () => {
            this.qualityOptionsBox.classList.toggle(DISABLED);
        })
        document.addEventListener('click', (e) => {
            if (e.target !== this.qualityOptionsBox && e.target !== this.qualityBtn) {
                this.qualityOptionsBox.classList.add(DISABLED);
            }
        })
    }

    addEventChangeQuality(hls, playPause, loadingSpinner) {

        // 화질 선택 버튼 이벤트
        const qualityOptions = this.qualityOptionsBox.querySelectorAll('button');
        qualityOptions.forEach(button => {
            button.addEventListener('click', () => {
                const quality = parseInt(button.getAttribute('data-quality'));

                if (quality === this.currentQuality) {
                    this.qualityOptionsBox.classList.add(DISABLED);
                    return;
                }

                // 화질 변경 시 로딩 표시만 하고 비디오는 계속 재생
                loadingSpinner.classList.remove(DISABLED);

                this.currentQuality = quality;
                hls.nextLevel = quality;  // currentLevel 대신 nextLevel 사용 : 다음 segment 부터 변경된 quality 적용
                this.updateQualityButton(quality);

                const onFragBuffered = (event, data) => {
                    // -1 : AUTO
                    if (quality === -1 || data.frag.level === quality) {
                        loadingSpinner.classList.add(DISABLED);
                        hls.off(Hls.Events.FRAG_BUFFERED, onFragBuffered);
                    }
                };

                hls.on(Hls.Events.FRAG_BUFFERED, onFragBuffered);
            });
        });
    }



}
class VideoVolume {

    constructor(video, volumeBtn, volumeSlider, volumeProgress) {
        this.video = video;
        this.volumeBtn = volumeBtn;
        this.volumeSlider = volumeSlider;
        this.volumeProgress = volumeProgress;
        // 드래그를 통한 볼륨 조절 기능
        this.isDraggingVolume = false;

        this.addEvent();

    }

    #loadLocalVolumeData() {
        const savedVolume = localStorage.getItem('videoVolume');
        if (savedVolume !== null) {
            this.video.volume = parseFloat(savedVolume);
            this.#drawVolumeProgress(this.video.volume);
        }
        this.updateVolumeIcon(this.video.volume);
    }

    addEvent() {
        // Initialize volume display
        this.video.addEventListener('loadedmetadata', () => {
            this.#loadLocalVolumeData();
        });

        // 볼륨 음소거 토글
        this.volumeBtn.addEventListener('click', () => {
            this.video.muted = !this.video.muted;
            this.updateVolumeIcon(this.video.muted ? 0 : this.video.volume);
            this.#drawVolumeProgress(this.video.muted ? 0 : this.video.volume);
        });

        // 볼륨 조절
        this.volumeSlider.addEventListener('click', (e) => {
            const pos = this.#getPos(e);
            this.video.volume = pos;
            this.#drawVolumeProgress(pos);
            this.updateVolumeIcon(pos);
        });

        // 볼륨 슬라이더 드래그 이벤트
        this.volumeSlider.addEventListener('mousedown', (e) => {
            this.isDraggingVolume = true;
            this.handleVolumeChange(e);
        });

        document.addEventListener('mousemove', (e) => {
            if (this.isDraggingVolume) this.handleVolumeChange(e);
        });

        document.addEventListener('mouseup', () => {
            this.isDraggingVolume = false;
        });
    }

    // 볼륨 아이콘 업데이트
    updateVolumeIcon(volume) {
        const volumeLevel = Math.floor(volume * 100);
        if (volumeLevel === 0) {
            this.volumeBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64l0 384c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352 64 352c-35.3 0-64-28.7-64-64l0-64c0-35.3 28.7-64 64-64l67.8 0L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM425 167l55 55 55-55c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9l-55 55 55 55c9.4 9.4 9.4 24.6 0 33.9s-24.6 9.4-33.9 0l-55-55-55 55c-9.4 9.4-24.6 9.4-33.9 0s-9.4-24.6 0-33.9l55-55-55-55c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0z"/></svg>';
        } else if (volumeLevel <= 50) {
            this.volumeBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64l0 384c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352 64 352c-35.3 0-64-28.7-64-64l0-64c0-35.3 28.7-64 64-64l67.8 0L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM412.6 181.5C434.1 199.1 448 225.9 448 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C393.1 284.4 400 271 400 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5z"/></svg>';
        } else {
            this.volumeBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M333.1 34.8C312.6 40 320 51.4 320 64l0 384c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352 64 352c-35.3 0-64-28.7-64-64l0-64c0-35.3 28.7-64 64-64l67.8 0L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zm172 72.2c43.2 35.2 70.9 88.9 70.9 149s-27.7 113.8-70.9 149c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C507.3 341.3 528 301.1 528 256s-20.7-85.3-53.2-111.8c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5zm-60.5 74.5C466.1 199.1 480 225.9 480 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C425.1 284.4 432 271 432 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5z"/></svg>';
        }
    }

    handleVolumeChange(e) {
        let pos = this.#getPos(e);
        this.video.volume = pos;
        this.#drawVolumeProgress(pos);
        this.updateVolumeIcon(pos);

        // 볼륨 설정 저장
        localStorage.setItem('videoVolume', this.video.volume);
    }

    #drawVolumeProgress(volume) {
        this.volumeProgress.style.width = (volume * 100) + '%';
    }

    #getPos(e) {
        const rect = this.volumeSlider.getBoundingClientRect();
        let pos = (e.clientX - rect.left) / rect.width;
        return Math.max(0, Math.min(1, pos)); // Clamp between 0 and 1
    }


}
class VideoTimeline {

    constructor(video, timelineElement, progressElement, timeDisplay) {
        this.video = video;
        this.timeline = timelineElement;
        this.progress = progressElement;
        this.bufferProgress = this.timeline.querySelector('.buffer-progress');
        this.timeDisplay = timeDisplay;
        this.isDraggingProgress = false;
        this.currentTimelineLocation = 0;
        this.#addEvent();
    }

    setDraggingEvent(playPause) {

        // 버퍼링 상태 업데이트
        this.video.addEventListener('progress', () => {
            this.#updateBufferProgress();
        });

        // 타임라인 드래그 이벤트
        this.timeline.addEventListener('mousedown', (e) => {
            this.isDraggingProgress = true;
            playPause.pause();
            this.currentTimelineLocation = this.#handleProgressChange(e);
        });

        document.addEventListener('mousemove', (e) => {
            if (this.isDraggingProgress) {
                this.currentTimelineLocation = this.#handleProgressChange(e);
                this.percentage(this.currentTimelineLocation, this.video.duration);
            }
        });

        document.addEventListener('mouseup', () => {
            if (this.isDraggingProgress) {
                this.isDraggingProgress = false;
                this.video.currentTime = this.currentTimelineLocation;
                playPause.play();
            }
        });
    }

    #addEvent() {
        // 타임라인 클릭
        this.timeline.addEventListener('click', (e) => {
            this.video.currentTime = this.#handleProgressChange(e);
        });

        // 타임라인 업데이트
        this.video.addEventListener('timeupdate', () => {
            this.percentage(this.video.currentTime,this.video.duration);
        });


    }

    #handleProgressChange(e) {
        const rect = this.timeline.getBoundingClientRect();
        const pos = (e.clientX - rect.left) / rect.width;
        return pos * this.video.duration;
    }

    #updateBufferProgress() {
        if (this.video.buffered.length > 0) {
            const bufferedEnd = this.video.buffered.end(this.video.buffered.length - 1);
            const duration = this.video.duration;
            this.bufferProgress.style.width = ((bufferedEnd / duration) * 100) + '%';
        }
    }

    percentage(current, total) {
        this.timeDisplay.textContent = `${this.#formatTime(current)} / ${this.#formatTime(total)}`;
        this.progress.style.width = (current / total) * 100 + '%';
    }

    // 시간 포맷팅
    #formatTime(seconds) {
        if (isNaN(seconds)) return '--:--';
        const minutes = Math.floor(seconds / 60);
        seconds = Math.floor(seconds % 60);
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    }
}
class VideoFullScreen {

    constructor(video, fullscreenBtn) {
        this.video = video;
        this.fullScreen = fullscreenBtn;
        this.#addEvent();
    }

    #addEvent() {
        this.fullScreen.addEventListener('click', () => this.#toggleFullScreen());
    }

    keyShortcuts(e) {
        if (e.code === KEYS.FULL_SCREEN) {
            this.#toggleFullScreen();
        }
    }

    #toggleFullScreen() {
        document.fullscreenElement ? this.#fullScreenExit() : this.#fullScreen();
    }
    #fullScreen() {
        this.video.parentElement.requestFullscreen();
    }
    #fullScreenExit() {
        document.exitFullscreen();
    }

}

window.addEventListener('load', () => {
    const videoElement = document.querySelector('#videoPlayer');
    const videoId = videoElement.getAttribute('aria-id');
    const playlistUrl = `/videos/${videoId}/master.m3u8`;

    const video = new Video(videoElement, playlistUrl);

    video.controller.setPanel(
        document.querySelector('.panel')
    );
    video.controller.setQuality(
        document.querySelector('.quality-btn'),
        document.querySelector('.quality-options')
    )
    video.controller.setPlayPauseBtn(
        document.querySelector('.play-pause'),
        document.querySelector('.play-pause-event')
    );
    video.controller.setVolume(
        document.querySelector('.volume-btn'),
        document.querySelector('.volume-slider'),
        document.querySelector('.volume-progress')
    )
    video.controller.setFullScreen(
        document.querySelector('.fullscreen')
    );
    video.controller.setTimeline(
        document.querySelector('.timeline'),
        document.querySelector('.progress'),
        document.querySelector('.time-display')
    )
    video.controller.setKeyShortCuts();
    video.initialEventListener();

});