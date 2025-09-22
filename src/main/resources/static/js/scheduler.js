(function(){
    const list = document.querySelector('#time-list');
    const addBtn = document.querySelector('#add-time');
    const modeSelect = document.querySelector('#mode-select');

    function createItem(value=''){
        const div = document.createElement('div');
        div.className = 'time-item';
        div.innerHTML = `
      <input class="input" name="times" placeholder="${modeSelect.value==='absolute'?'HH:mm':'Cron: m h dom mon dow'}" value="${value}">
      <select class="input" name="mode">
        <option value="absolute" ${modeSelect.value==='absolute'?'selected':''}>Абсолют</option>
        <option value="cron" ${modeSelect.value==='cron'?'selected':''}>Cron</option>
      </select>
      <button type="button" class="btn danger">×</button>
    `;
        div.querySelector('.btn.danger').addEventListener('click', ()=> div.remove());
        return div;
    }

    if(addBtn && list){
        addBtn.addEventListener('click', ()=> list.appendChild(createItem()));
    }
})();
