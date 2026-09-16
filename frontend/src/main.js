import './style.css'

const state = { keyword: '', page: 1, totalPages: 1 }
const list = document.querySelector('#bookList')
const notice = document.querySelector('#notice')
const dialog = document.querySelector('#bookDialog')
const form = document.querySelector('#bookForm')

async function request(url, options = {}) {
  const response = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...options })
  if (response.status === 204) return null
  const body = await response.json()
  if (!response.ok) throw new Error(body.message || '请求失败')
  return body
}

async function loadBooks() {
  list.innerHTML = '<p class="loading">正在翻阅书架…</p>'
  try {
    const query = new URLSearchParams({ keyword: state.keyword, page: String(state.page) })
    const data = await request(`/api/books?${query}`)
    state.page = data.page
    state.totalPages = data.totalPages
    document.querySelector('#countBadge').textContent = `${data.total} 本藏书`
    document.querySelector('#pageLabel').textContent = String(data.page).padStart(2, '0')
    renderBooks(data.items)
    renderPager()
  } catch (error) { showNotice(error.message, true); list.innerHTML = '' }
}

function renderBooks(books) {
  if (!books.length) { list.innerHTML = '<p class="empty">没有匹配的书。换个关键词试试。</p>'; return }
  list.innerHTML = books.map((book, index) => `
    <article class="book" style="--delay:${index * 55}ms">
      <span class="number">${String(book.id).padStart(3, '0')}</span>
      <div><h2>${escapeHtml(book.title)}</h2><p>${escapeHtml(book.author)}</p></div>
      <p class="price">¥${Number(book.price).toFixed(2)}</p>
      <p class="stock">余量<br><strong>${book.stock}</strong></p>
      <div class="actions"><button data-edit="${book.id}">编辑</button><button data-delete="${book.id}" class="danger">删除</button></div>
    </article>`).join('')
}

function renderPager() {
  document.querySelector('#pager').innerHTML = Array.from({ length: state.totalPages }, (_, index) => {
    const page = index + 1
    return `<button data-page="${page}" class="${page === state.page ? 'active' : ''}">${String(page).padStart(2, '0')}</button>`
  }).join('')
}

function openForm(book = null) {
  form.reset(); document.querySelector('#formError').textContent = ''
  document.querySelector('#bookId').value = book?.id ?? ''
  document.querySelector('#formKicker').textContent = book ? `EDITING #${book.id}` : 'NEW ENTRY'
  document.querySelector('#formTitle').textContent = book ? '编辑书目' : '登记新书'
  if (book) ['title', 'author', 'price', 'stock'].forEach((key) => { document.querySelector(`#${key}`).value = book[key] })
  dialog.showModal()
}

function showNotice(message, isError = false) { notice.textContent = message; notice.classList.toggle('error', isError) }
function escapeHtml(value) { return String(value).replace(/[&<>'"]/g, (char) => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', "'":'&#39;', '"':'&quot;' })[char]) }

document.querySelector('#searchForm').addEventListener('submit', (event) => { event.preventDefault(); state.keyword = document.querySelector('#keyword').value.trim(); state.page = 1; loadBooks() })
document.querySelector('#resetButton').addEventListener('click', () => { document.querySelector('#keyword').value = ''; state.keyword = ''; state.page = 1; loadBooks() })
document.querySelector('#createButton').addEventListener('click', () => openForm())
document.querySelector('#cancelButton').addEventListener('click', () => dialog.close())
document.querySelector('#pager').addEventListener('click', (event) => { const page = event.target.dataset.page; if (page) { state.page = Number(page); loadBooks() } })
list.addEventListener('click', async (event) => {
  const id = event.target.dataset.edit || event.target.dataset.delete
  if (!id) return
  try {
    if (event.target.dataset.edit) openForm(await request(`/api/books/${id}`))
    if (event.target.dataset.delete && confirm('确定删除这本书吗？')) { await request(`/api/books/${id}`, { method: 'DELETE' }); showNotice('书籍已删除'); loadBooks() }
  } catch (error) { showNotice(error.message, true) }
})
form.addEventListener('submit', async (event) => {
  event.preventDefault()
  const id = document.querySelector('#bookId').value
  const payload = Object.fromEntries(['title', 'author', 'price', 'stock'].map((key) => [key, document.querySelector(`#${key}`).value]))
  try { await request(id ? `/api/books/${id}` : '/api/books', { method: id ? 'PUT' : 'POST', body: JSON.stringify(payload) }); dialog.close(); showNotice(id ? '书籍已更新' : '新书已登记'); loadBooks() }
  catch (error) { document.querySelector('#formError').textContent = error.message }
})

loadBooks()
