import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

async function request(url, options = {}) {
  const response = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...options })
  if (response.status === 204) return null
  const body = await response.json()
  if (!response.ok) throw new Error(body.message || '请求失败')
  return body
}

function BookRow({ book, onAdd, onEdit, onDelete }) {
  return <article className="book">
    <span className="number">{String(book.id).padStart(3, '0')}</span>
    <div><h2>{book.title}</h2><p>{book.author}</p></div>
    <p className="price">¥{Number(book.price).toFixed(2)}</p>
    <p className="stock">余量<br /><strong>{book.stock}</strong></p>
    <div className="book-tools">
      <button className="add-to-cart" disabled={book.stock <= 0} onClick={() => onAdd(book)}>{book.stock <= 0 ? '暂缺' : '加入清单'}</button>
      <div className="actions"><button onClick={() => onEdit(book.id)}>编辑</button><button className="danger" onClick={() => onDelete(book.id)}>删除</button></div>
    </div>
  </article>
}

function OrderDesk({ cart, orders, onChangeQuantity, onCheckout, onPay, onCancel }) {
  const total = useMemo(() => cart.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0), [cart])
  const count = cart.reduce((sum, item) => sum + item.quantity, 0)
  return <aside className="order-desk" aria-label="订单操作">
    <div className="desk-heading"><p>YOUR STACK</p><span>{count} 本</span></div>
    <h2>采购清单</h2>
    {!cart.length ? <p className="cart-empty">从左侧书架挑选你想带走的书。</p> : <>
      <div className="cart-items">{cart.map((item) => <div className="cart-item" key={item.id}>
        <div><strong>{item.title}</strong><span>¥{Number(item.price).toFixed(2)} / 本</span></div>
        <div className="quantity"><button aria-label={`减少 ${item.title}`} onClick={() => onChangeQuantity(item.id, -1)}>−</button><output>{item.quantity}</output><button aria-label={`增加 ${item.title}`} onClick={() => onChangeQuantity(item.id, 1)}>＋</button></div>
      </div>)}</div>
      <p className="cart-total"><span>预计合计</span><strong>¥{total.toFixed(2)}</strong></p>
    </>}
    <button className="primary checkout" disabled={!cart.length} onClick={onCheckout}>创建订单并预占库存</button>
    {orders.map((order) => <section className="order-panel" key={order.id}>
      <div className="desk-heading"><p>ACTIVE ORDER</p></div>
      <p className="order-id">订单 #{order.id}</p><h3>待支付</h3>
      <ul>{order.items.map((item) => <li key={item.bookId}><span>{item.bookTitle} × {item.quantity}</span><strong>¥{Number(item.lineAmount).toFixed(2)}</strong></li>)}</ul>
      <p className="order-total"><span>合计</span><strong>¥{Number(order.totalAmount).toFixed(2)}</strong></p>
      <div className="order-actions"><button className="quiet" onClick={() => onCancel(order.id)}>取消订单</button><button className="primary" onClick={() => onPay(order.id)}>模拟支付</button></div>
    </section>)}
  </aside>
}

function BookDialog({ draft, error, onClose, onSubmit }) {
  if (!draft) return null
  return <div className="modal-backdrop" role="presentation"><form className="modal" onSubmit={onSubmit}>
    <div className="dialog-title"><p>{draft.id ? `EDITING #${draft.id}` : 'NEW ENTRY'}</p><h2>{draft.id ? '编辑书目' : '登记新书'}</h2></div>
    <label>书名<input name="title" defaultValue={draft.title} maxLength="100" required /></label>
    <label>作者<input name="author" defaultValue={draft.author} maxLength="60" required /></label>
    <div className="two-columns"><label>价格<input name="price" type="number" defaultValue={draft.price} min="0" step="0.01" required /></label><label>库存<input name="stock" type="number" defaultValue={draft.stock} min="0" step="1" required /></label></div>
    <p className="form-error" aria-live="polite">{error}</p>
    <footer><button type="button" className="quiet" onClick={onClose}>取消</button><button className="primary" type="submit">保存书目</button></footer>
  </form></div>
}

function App() {
  const [keyword, setKeyword] = useState(''); const [page, setPage] = useState(1)
  const [catalogue, setCatalogue] = useState({ items: [], total: 0, totalPages: 1, page: 1 })
  const [cart, setCart] = useState([]); const [orders, setOrders] = useState([]); const [notice, setNotice] = useState('')
  const [draft, setDraft] = useState(null); const [formError, setFormError] = useState('')
  const loadBooks = useCallback(async () => {
    try { const [data, pendingOrders] = await Promise.all([request(`/api/books?${new URLSearchParams({ keyword, page: String(page) })}`), request('/api/orders/pending')]); setCatalogue(data); setPage(data.page); setOrders(pendingOrders) }
    catch (error) { setNotice({ text: error.message, error: true }) }
  }, [keyword, page])
  useEffect(() => { loadBooks() }, [loadBooks])
  const addToCart = (book) => { setCart((current) => { const found = current.find((item) => item.id === book.id); return found ? current.map((item) => item.id === book.id ? { ...item, quantity: item.quantity + 1 } : item) : [...current, { ...book, quantity: 1 }] }); setNotice({ text: `《${book.title}》已加入采购清单` }) }
  const changeQuantity = (id, delta) => setCart((current) => current.flatMap((item) => item.id !== id ? [item] : item.quantity + delta > 0 ? [{ ...item, quantity: item.quantity + delta }] : []))
  const deleteBook = async (id) => { if (!confirm('确定删除这本书吗？')) return; try { await request(`/api/books/${id}`, { method: 'DELETE' }); setNotice({ text: '书籍已删除' }); loadBooks() } catch (error) { setNotice({ text: error.message, error: true }) } }
  const checkout = async () => { try { await request('/api/orders', { method: 'POST', body: JSON.stringify({ items: cart.map(({ id, quantity }) => ({ bookId: id, quantity })) }) }); setCart([]); setNotice({ text: '订单已创建，库存已预占' }); loadBooks() } catch (error) { setNotice({ text: error.message, error: true }) } }
  const transitionOrder = async (id, action, message) => { try { await request(`/api/orders/${id}/${action}`, { method: 'POST' }); setNotice({ text: message }); loadBooks() } catch (error) { setNotice({ text: error.message, error: true }) } }
  const submitBook = async (event) => { event.preventDefault(); const payload = Object.fromEntries(new FormData(event.currentTarget)); try { await request(draft.id ? `/api/books/${draft.id}` : '/api/books', { method: draft.id ? 'PUT' : 'POST', body: JSON.stringify(payload) }); setDraft(null); setNotice({ text: draft.id ? '书籍已更新' : '新书已登记' }); loadBooks() } catch (error) { setFormError(error.message) } }
  return <main className="shell"><header className="masthead"><p className="eyebrow">THE PRACTICE BOOKSHOP · JAVA WEB</p><div className="heading-row"><h1>页间书屋</h1><span>{catalogue.total} 本藏书</span></div><p className="subhead">从书架挑选、预占库存，再完成一笔轻量的模拟购书。</p></header>
    <section className="toolbar"><form className="search-form" onSubmit={(event) => { event.preventDefault(); setPage(1) }}><label>检索藏书<input value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder="书名或作者" /></label><button type="submit">搜索</button><button type="button" className="quiet" onClick={() => { setKeyword(''); setPage(1) }}>重置</button></form><button className="primary" onClick={() => { setFormError(''); setDraft({ title: '', author: '', price: '', stock: '' }) }}>＋ 登记新书</button></section>
    <p className={`notice ${notice.error ? 'error' : ''}`} aria-live="polite">{notice.text}</p><div className="shop-layout"><section className="catalogue"><div className="catalogue-label">CATALOGUE / {String(catalogue.page).padStart(2, '0')}</div>{catalogue.items.length ? catalogue.items.map((book) => <BookRow key={book.id} book={book} onAdd={addToCart} onEdit={async (id) => { try { setDraft(await request(`/api/books/${id}`)); setFormError('') } catch (error) { setNotice({ text: error.message, error: true }) } }} onDelete={deleteBook} />) : <p className="empty">没有匹配的书。换个关键词试试。</p>}<nav className="pager">{Array.from({ length: catalogue.totalPages }, (_, index) => <button key={index} className={index + 1 === catalogue.page ? 'active' : ''} onClick={() => setPage(index + 1)}>{String(index + 1).padStart(2, '0')}</button>)}</nav></section><OrderDesk cart={cart} orders={orders} onChangeQuantity={changeQuantity} onCheckout={checkout} onPay={(id) => transitionOrder(id, 'pay', '支付已确认，订单完成')} onCancel={(id) => transitionOrder(id, 'cancel', '订单已取消，库存已释放')} /></div><BookDialog draft={draft} error={formError} onClose={() => setDraft(null)} onSubmit={submitBook} /></main>
}

createRoot(document.getElementById('root')).render(<App />)
