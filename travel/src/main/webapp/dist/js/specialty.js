let dataCache = {
	specialty: {pageNo : 0, items: [], totalCount: 0, isEnd: false },
	restaurant: {pageNo : 0, items: [], totalCount: 0, isEnd: false }
}


let myScrap = new Set();

let isDataLoaded = false;

let tab, grid, searchBtn, searchInput, spinner;


function startNewSearch(type) {
    if (!type) return;

    // 1. 해당 탭의 캐시를 초기화합니다.
    dataCache[type] = { pageNo: 1, items: [], totalCount: 0, isEnd: false };
    
    // 2. 탭 종류에 따라 적절한 load 함수를 1페이지부터 호출합니다.
    if (type === 'specialty') {
        loadSpecialties(1, false);
    } else if (type === 'restaurant') {
        loadRestaurants(1, false);
    }
}

async function fetchMyScrapIds() {
    try {
        const response = await fetch(`${CONTEXT_PATH}/tour/apiIds`);
        if (!response.ok) return;

        const scrapList = await response.json();
        myScrap = new Set(scrapList);
    } catch (error) {
        console.error("스크랩 목록 로딩 실패:", error);
    }
}
	

	
// 특산물 API를 호출하는 함수
async function loadSpecialties(pageNo = 1, append = false) {
	const grid = document.querySelector('#specialty-grid');
	const spinner = document.querySelector('#specialty-spinner');
	const searchInput = document.querySelector('#specialtySearchKeyword');

    isDataLoaded = true;
    spinner.classList.remove('d-none');
	
	const keyword = searchInput.value;
	
	if (!append) {
	        grid.innerHTML = '';
	    }
	
	let url;
	let isTourApi;
	
	// 지역에 따라 호출할 api를 분리(농촌진흥청 특산물 api에 서울, 세종, 제주 특산물 데이터가 없어 국문관광공사 api의 음식점으로 대체)
	if(selectedRegion.code == '1' || selectedRegion.code == '8' || selectedRegion.code == '39') {
		isTourApi = true;
		url = `${CONTEXT_PATH}/location/specialtyView?isTourApi=true&contentTypeId=39&keyword=${encodeURIComponent(keyword)}&areaCode=${selectedRegion.code}&pageNo=${pageNo}`;
	} else {
		isTourApi = false;
	    url = `${CONTEXT_PATH}/location/specialtyView?keyword=${encodeURIComponent(keyword)}&areaName=${encodeURIComponent(selectedRegion.name)}&pageNo=${pageNo}`;		
	}
	

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
		
		let items = [];
		let totalCount = 0;
		
		if(isTourApi) {
			const jsonData = await response.json();
			if(jsonData.response.header.resultCode === "0000" && jsonData.response.body.items) {
				const rawItems = jsonData.response.body.items.item;
				const itemList = Array.isArray(rawItems) ? rawItems : (rawItems ? [rawItems] : []);
				totalCount = jsonData.response.body.totalCount;
				
				items = itemList.map(item => ({
					apiSource: 'tourAPI',
					title: item.title,
					imageUrl: item.firstimage || '',
					areaName: item.addr1,
					contentId: item.contentid,
					contentTypeId: item.contenttypeid
				}));
			}
		} else {		
	        const xmlString = await response.text();
	        const parser = new DOMParser();
	        const xmlDoc = parser.parseFromString(xmlString, "text/xml");
			const xmlItems = xmlDoc.getElementsByTagName('item');
			totalCount = xmlDoc.getElementsByTagName('totalCount')[0].textContent;
			
			for(let i = 0; i < xmlItems.length; i++) {
				const item = xmlItems[i];
				items.push({
					apiSource: 'nongsaro',
					title: (item.getElementsByTagName('cntntsSj')[0]?.childNodes[0]?.nodeValue) || '이름 없음',
                    imageUrl: (item.getElementsByTagName('imgUrl')[0]?.childNodes[0]?.nodeValue) || '',
                    areaName: (item.getElementsByTagName('areaNm')[0]?.childNodes[0]?.nodeValue) || '지역 정보 없음',
					linkUrl: (item.getElementsByTagName('linkUrl')[0]?.childNodes[0]?.nodeValue) || '#'
				})
			}
		}
		
		if (append) {
		            dataCache.specialty.items.push(...items); // 기존 목록에 추가
		        } else {
		            dataCache.specialty.items = items; // 새로 덮어쓰기
		        }
		        dataCache.specialty.pageNo = pageNo;
		        dataCache.specialty.totalCount = Number(totalCount);
		        dataCache.specialty.isEnd = dataCache.specialty.items.length >= totalCount;
        
        // 최종 파싱 함수 호출
        displayCards(items, grid, append);

    } catch (error) {
        console.error('데이터 로딩 실패:', error);
        grid.innerHTML = `<p class="text-center text-danger">데이터를 불러오는 중 오류가 발생했습니다.</p>`;
    } finally {
        spinner.classList.add('d-none');
    }
}

// 음식점 api를 호출하는 함수
async function loadRestaurants(pageNo = 1, append = false) {
	const grid = document.querySelector('#restaurant-grid');
	const spinner = document.querySelector('#restaurant-spinner');
	const searchInput = document.querySelector('#restaurantSearchKeyword');

    isDataLoaded = true;
    spinner.classList.remove('d-none');
	
	const keyword = searchInput.value;
	if (!append) {
	        grid.innerHTML = '';
	    }
	
	const url = `${CONTEXT_PATH}/location/restaurantView?contentTypeId=39&keyword=${encodeURIComponent(keyword)}&areaCode=${selectedRegion.code}&pageNo=${pageNo}`;
	
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
		
		let items = [];
		let totalCount = 0;
		
		const jsonData = await response.json();
		if(jsonData.response.header.resultCode === "0000" && jsonData.response.body.items) {
			const rawItems = jsonData.response.body.items.item;
			const itemList = Array.isArray(rawItems) ? rawItems : (rawItems ? [rawItems] : []);
			totalCount = jsonData.response.body.totalCount;
			
			items = itemList.map(item => ({
				apiSource: 'tourAPI',
				title: item.title,
				imageUrl: item.firstimage || '',
				areaName: item.addr1,
				tel: item.tel || '전화번호 정보 없음',
				contentId: item.contentid,
				contentTypeId: item.contenttypeid
			}));
			
		}
		
		if (append) {
		            dataCache.restaurant.items.push(...items); // 기존 목록에 추가
		        } else {
		            dataCache.restaurant.items = items; // 새로 덮어쓰기
		        }
		        dataCache.restaurant.pageNo = pageNo;
		        dataCache.restaurant.totalCount = Number(totalCount);
		        dataCache.restaurant.isEnd = dataCache.restaurant.items.length >= totalCount;
        
        // 최종 파싱 함수 호출
        displayCards(items, grid, append);

    } catch (error) {
        console.error('데이터 로딩 실패:', error);
        grid.innerHTML = `<p class="text-center text-danger">데이터를 불러오는 중 오류가 발생했습니다.</p>`;
    } finally {
        spinner.classList.add('d-none');
    }
}

//  items를 받아 HTML 카드를 만드는 함수
function displayCards(items, selectedTab, append = false) {	
	
	const svgTemplate = `<svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%" viewBox="0 0 400 220"><rect width="400" height="220" fill="#f0f2f5"/></svg>`;
	const placeholderSvg = `data:image/svg+xml,${encodeURIComponent(svgTemplate)}`;
	
    let cardsHtml = '';

    if (items.length === 0 && !append) {
        selectedTab.innerHTML = '<p class="text-center text-muted">표시할 항목이 없습니다.</p>';
        return;
    }

	for (const item of items) {
		
		const itemJsonString = JSON.stringify(item).replace(/"/g, "'");
		
		let scrapHtml = '';
		
		if(selectedTab.id === 'restaurant-grid') {
			
            const apiId = item.contentId || item.title;
			const isScrapped = apiId ? myScrap.has(apiId.toString()) : false;
			const starClass = isScrapped ? 'bi-star-fill' : 'bi-star';
            const apiTypeId = item.contentTypeId || '39';
			
			scrapHtml = `
			                <div class="scrap-star" 
			                     data-apiid="${apiId}" 
			                     data-apitypeid="${apiTypeId}" 
			                     data-addr="${item.areaName}" 
			                     data-title="${item.title}" 
			                     data-image="${item.imageUrl}"
			                     data-typename="음식점">
			                    <i class="bi ${starClass}"></i>
			                </div>
			            `;
		}
		
	        cardsHtml += `
	            <div class="col-md-4 col-sm-6 mb-4"> 
	                <div class="card h-100 info-card" onclick="showDetailModal(event, ${itemJsonString})">
	                    <div class="card-img-container">
	                        <img src="${item.imageUrl}" class="card-img-top" alt="${item.title}" onerror="this.onerror=null; this.src='${placeholderSvg}'">
							${scrapHtml}
	                    </div>
	                    <div class="card-body">
	                        <h5 class="card-title">${item.title}</h5>
	                        <p class="card-text text-muted">${item.areaName}</p>
	                    </div>
	                </div>
	            </div>
	        `;
	    }
		
		if (append) {
		        selectedTab.insertAdjacentHTML('beforeend', cardsHtml); // 기존 내용 뒤에 추가
		    } else {
		        selectedTab.innerHTML = cardsHtml; // 전체를 새로 덮어쓰기
		    }
		
			// 어떤 탭의 카드인지 알아내서, 해당 탭의 캐시와 더보기 버튼을 제어
			   let tabType = '';
			   if (selectedTab.id === 'specialty-grid') {
			       tabType = 'specialty';
			   } else if (selectedTab.id === 'restaurant-grid') {
			       tabType = 'restaurant';
			   }
			   
			   if (tabType) {
			       const moreBtn = document.querySelector(`#${tabType}-more-btn`);
			       const cache = dataCache[tabType];
			       
			       // isEnd는 load 함수에서 미리 계산해 둔 상태 값
			       if (cache.isEnd) {
			           moreBtn.style.display = 'none';
			       } else {
			           moreBtn.style.display = 'block';
			       }
			   }
	    
}

// 상세 정보 모달을 띄우는 함수
async function showDetailModal(event, itemData) {
	
	if (event.target.closest('.scrap-star')) {
	       return; // 스크랩 버튼을 누른 것이므로, 모달을 띄우지 않고 함수 종료
	   }
	
    const modalEl = document.getElementById('itemDetailModal');
    if (!modalEl) return;
    
    document.getElementById('modal-item-title').textContent = itemData.title || '정보 없음';
    
    const modalImg = document.getElementById('modal-item-image');
	
	if(itemData.imageUrl) {
		modalImg.innerHTML = `<img src="${itemData.imageUrl}" class="img-fluid rounded" style="max-height: 300px; object-fit: contain;">`;
	} else {
		modalImg.innerHTML = '';
	}

    document.getElementById('modal-item-addr').querySelector('span').textContent = itemData.areaName || '정보 없음';
    document.getElementById('modal-item-tel').querySelector('span').textContent = itemData.tel || '정보 없음';

    const overviewEl = document.getElementById('modal-item-overview');
    overviewEl.innerHTML = '<div class="spinner-border spinner-border-sm"></div> 상세 설명 로딩 중...';
    
    // API 출처에 따라 상세 정보를 다르게 불러오기
    if (itemData.apiSource === 'tourAPI' && itemData.contentId) {
        // 국문관광정보 API의 contentId로 상세정보 추가 조회
        try {
            // 상세정보 조회하러 컨트롤러로
            const detailUrl = `${CONTEXT_PATH}/location/itemDetail?contentId=${itemData.contentId}`;
            const resp = await fetch(detailUrl);
            const detailData = await resp.json();
            const overview = detailData?.response?.body?.items?.item?.[0]?.overview || '상세 정보가 없습니다.';
            overviewEl.innerHTML = overview;
        } catch (error) {
            console.error("상세 정보 로드 실패:", error);
            overviewEl.innerHTML = '상세 정보를 불러오는 데 실패했습니다.';
        }
    } else if (itemData.apiSource === 'nongsaro' && itemData.linkUrl) {
        // 농촌진흥청 API: linkUrl로 '자세히 보기' 버튼 제공
        overviewEl.innerHTML = `<a href="${itemData.linkUrl}" class="btn btn-primary" target="_blank">웹사이트에서 자세히 보기</a>`;
    } else {
        overviewEl.innerHTML = '추가 정보가 없습니다.';
    }

    const modal = new bootstrap.Modal(modalEl);
    modal.show();
}


document.addEventListener('DOMContentLoaded', async function() {
    
	await fetchMyScrapIds();
	
	/**
     * 각 탭에 필요한 이벤트(탭 클릭, 검색, 엔터)를 자동으로 등록해주는 함수
     * @param {string} tabId - 탭 버튼의 ID (예: '#specialty-tab')
     * @param {string} searchInputId - 검색창의 ID
     * @param {string} searchBtnId - 검색 버튼의 ID
     */
	
    function setupTabEvents(tabId, searchInputId, searchBtnId, type) {
        const tab = document.querySelector(tabId);
        const searchInput = document.querySelector(searchInputId);
        const searchBtn = document.querySelector(searchBtnId);
   

        tab.addEventListener('shown.bs.tab', function () {
          	searchInput.value = '';
			startNewSearch(type);
        });

        searchBtn.addEventListener('click', () => startNewSearch(type)); // 검색 버튼 클릭 시 새로 검색
        searchInput.addEventListener('keypress', e => {
            if (e.key === 'Enter') startNewSearch(type); // 엔터 키 입력 시 새로 검색
        });
		
    }

	    // 각 탭에 대해 함수를 호출
	    setupTabEvents('#specialty-tab', '#specialtySearchKeyword', '#specialtySearchBtn', 'specialty');
	    setupTabEvents('#restaurant-tab', '#restaurantSearchKeyword', '#restaurantSearchBtn', 'restaurant');
		
		document.querySelector('#specialty-more-btn').addEventListener('click', () => {
		        const nextPage = dataCache.specialty.pageNo + 1;
		        loadSpecialties(nextPage, true); // append: true로 다음 페이지 로드
		    });

	    document.querySelector('#restaurant-more-btn').addEventListener('click', () => {
	        const nextPage = dataCache.restaurant.pageNo + 1;
	        loadRestaurants(nextPage, true); // append: true로 다음 페이지 로드
	    });
	
});
