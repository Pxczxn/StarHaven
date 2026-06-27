import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { getAvailableRooms } from '../../api/room';
import { formatDate, getDaysDiff } from '../../utils/format';
import './index.css';

const weekDays = ['一', '二', '三', '四', '五', '六', '日'];

function FilterSelect({ label, value, options, onChange }) {
  const [open, setOpen] = useState(false);
  const current = options.find((option) => option.value === value) || options[0];

  return (
    <div className={`filter-field filter-select ${open ? 'is-open' : ''}`}>
      <span>{label}</span>
      <button
        type="button"
        className="filter-select-trigger"
        onClick={() => setOpen((next) => !next)}
        onBlur={() => window.setTimeout(() => setOpen(false), 120)}
      >
        <strong>{current.label}</strong>
        <i>⌄</i>
      </button>
      {open && (
        <div className="filter-select-menu cosmic-dropdown-panel">
          {options.map((option) => (
            <button
              key={option.value || 'all'}
              type="button"
              className={`cosmic-dropdown-item ${option.value === value ? 'is-selected' : ''}`}
              onMouseDown={(event) => event.preventDefault()}
              onClick={() => {
                onChange(option.value);
                setOpen(false);
              }}
            >
              {option.label}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

function buildCalendarDays(monthDate) {
  const start = dayjs(monthDate).startOf('month');
  const end = dayjs(monthDate).endOf('month');
  const startOffset = (start.day() + 6) % 7;
  const days = [];

  for (let i = 0; i < startOffset; i += 1) {
    days.push(start.subtract(startOffset - i, 'day'));
  }

  for (let day = 1; day <= end.date(); day += 1) {
    days.push(start.date(day));
  }

  while (days.length % 7 !== 0 || days.length < 42) {
    days.push(days[days.length - 1].add(1, 'day'));
  }

  return days;
}

function CalendarMonth({ monthDate, minDate, selected, startDate, endDate, onSelect }) {
  const days = buildCalendarDays(monthDate);

  return (
    <div className="cosmic-calendar-month">
      <strong className="cosmic-calendar-month-title">{monthDate.format('YYYY年 M月')}</strong>

      <div className="cosmic-calendar-weekdays">
        {weekDays.map((day) => (
          <span key={day}>{day}</span>
        ))}
      </div>

      <div className="cosmic-calendar-grid">
        {days.map((date) => {
          const dateKey = date.format('YYYY-MM-DD');
          const isCurrentMonth = date.month() === monthDate.month();
          if (!isCurrentMonth) {
            return (
              <span
                key={dateKey}
                className="cosmic-date-cell is-empty"
                aria-hidden="true"
              />
            );
          }

          const isDisabled = date.isBefore(minDate, 'day');
          const isRangeStart = date.isSame(startDate, 'day');
          const isRangeEnd = date.isSame(endDate, 'day');
          const isInRange = date.isAfter(startDate, 'day') && date.isBefore(endDate, 'day');
          const isSelected = date.isSame(selected, 'day');
          const isLockedOnly = isDisabled && !isRangeStart && !isRangeEnd && !isInRange;

          return (
            <button
              type="button"
              key={dateKey}
              className={[
                'cosmic-date-cell',
                isLockedOnly ? 'disabled' : '',
                isInRange ? 'in-range' : '',
                isRangeStart ? 'selected-start' : '',
                isRangeEnd ? 'selected-end' : '',
                isSelected ? 'is-active-date' : '',
              ].filter(Boolean).join(' ')}
              disabled={isLockedOnly}
              onClick={() => onSelect(date)}
            >
              {date.date()}
            </button>
          );
        })}
      </div>
    </div>
  );
}

function DateCalendar({ value, min, rangeStart, rangeEnd, wheelLocked, onChange }) {
  const [visibleMonth, setVisibleMonth] = useState(dayjs(value || min).startOf('month'));
  const [wheelCaptured, setWheelCaptured] = useState(false);
  const [monthMotion, setMonthMotion] = useState('');
  const panelRef = useRef(null);
  const lastWheelAtRef = useRef(0);
  const monthMotionTimerRef = useRef(null);
  const minDate = dayjs(min);
  const selected = dayjs(value);
  const startDate = dayjs(rangeStart);
  const endDate = dayjs(rangeEnd);
  const nextMonth = visibleMonth.add(1, 'month');

  const handleSelect = (date) => {
    if (date.isBefore(minDate, 'day')) return;
    onChange(date.format('YYYY-MM-DD'));
  };

  const shiftMonth = (delta) => {
    setMonthMotion(delta > 0 ? 'slide-next' : 'slide-prev');
    setVisibleMonth((current) => current.add(delta, 'month'));

    if (monthMotionTimerRef.current) {
      clearTimeout(monthMotionTimerRef.current);
    }
    monthMotionTimerRef.current = setTimeout(() => {
      setMonthMotion('');
      monthMotionTimerRef.current = null;
    }, 280);
  };

  useEffect(() => {
    const panel = panelRef.current;
    if (!panel) return undefined;

    const handleWheel = (event) => {
      if (wheelLocked || !wheelCaptured) {
        return;
      }

      event.preventDefault();
      event.stopPropagation();

      const now = Date.now();
      if (now - lastWheelAtRef.current < 260 || Math.abs(event.deltaY) < 12) {
        return;
      }

      lastWheelAtRef.current = now;
      shiftMonth(event.deltaY > 0 ? 1 : -1);
    };

    panel.addEventListener('wheel', handleWheel, { passive: false });
    return () => panel.removeEventListener('wheel', handleWheel);
  }, [wheelCaptured, wheelLocked]);

  useEffect(() => {
    return () => {
      if (monthMotionTimerRef.current) {
        clearTimeout(monthMotionTimerRef.current);
      }
    };
  }, []);

  return (
    <div
      className="cosmic-datepicker-panel"
      ref={panelRef}
      tabIndex={-1}
      onMouseEnter={() => setWheelCaptured(true)}
      onMouseLeave={() => setWheelCaptured(false)}
      onFocus={() => setWheelCaptured(true)}
      onBlur={() => setWheelCaptured(false)}
    >
      <div className="cosmic-calendar-header">
        <button type="button" onClick={() => shiftMonth(-1)}>
          ‹
        </button>
        <strong>{visibleMonth.format('YYYY年 M月')} - {nextMonth.format('M月')}</strong>
        <button type="button" onClick={() => shiftMonth(1)}>
          ›
        </button>
      </div>

      <div
        key={`${visibleMonth.format('YYYY-MM')}-${monthMotion}`}
        className={`cosmic-calendar-range ${monthMotion}`}
      >
        <CalendarMonth
          monthDate={visibleMonth}
          minDate={minDate}
          selected={selected}
          startDate={startDate}
          endDate={endDate}
          onSelect={handleSelect}
        />
        <CalendarMonth
          monthDate={nextMonth}
          minDate={minDate}
          selected={selected}
          startDate={startDate}
          endDate={endDate}
          onSelect={handleSelect}
        />
      </div>
    </div>
  );
}

function Rooms() {
  const navigate = useNavigate();
  const today = new Date().toISOString().split('T')[0];
  const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];

  const [checkInDate, setCheckInDate] = useState(today);
  const [checkOutDate, setCheckOutDate] = useState(tomorrow);
  const [rooms, setRooms] = useState([]);
  const [filters, setFilters] = useState({
    floor: '',
    capacity: '',
    priceRange: '',
  });
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const [activeDate, setActiveDate] = useState('checkIn');
  const [calendarWheelLocked, setCalendarWheelLocked] = useState(false);

  const handleSearch = async () => {
    if (!checkInDate || !checkOutDate) {
      alert('请选择入住和离店日期');
      return;
    }

    if (checkInDate >= checkOutDate) {
      alert('离店日期必须晚于入住日期');
      return;
    }

    try {
      setLoading(true);
      setSearched(true);
      const data = await getAvailableRooms({
        checkInDate,
        checkOutDate,
      });
      setRooms(data);
    } catch (error) {
      console.error('加载失败:', error);
      alert(error.message || '房间加载失败');
    } finally {
      setLoading(false);
    }
  };

  const handleBook = (room) => {
    navigate(
      `/booking?roomId=${room.id}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}`
    );
  };

  const nights = getDaysDiff(checkInDate, checkOutDate);
  const floorOptions = useMemo(() => (
    Array.from(new Set(rooms.map((room) => room.floor).filter(Boolean))).sort((a, b) => `${a}`.localeCompare(`${b}`))
  ), [rooms]);
  const floorSelectOptions = useMemo(() => [
    { value: '', label: '不限楼层' },
    ...floorOptions.map((floor) => ({ value: floor, label: floor })),
  ], [floorOptions]);
  const capacitySelectOptions = [
    { value: '', label: '不限人数' },
    { value: '1', label: '1人及以上' },
    { value: '2', label: '2人及以上' },
    { value: '3', label: '3人及以上' },
    { value: '4', label: '4人及以上' },
  ];
  const priceSelectOptions = [
    { value: '', label: '不限价格' },
    { value: '0-299', label: '¥299 以下' },
    { value: '300-499', label: '¥300 - ¥499' },
    { value: '500-799', label: '¥500 - ¥799' },
    { value: '800-up', label: '¥800 以上' },
  ];
  const filteredRooms = useMemo(() => {
    const capacity = filters.capacity === '' ? null : Number(filters.capacity);
    const [minPrice, maxPrice] = filters.priceRange
      ? filters.priceRange.split('-').map((value) => (value === 'up' ? null : Number(value)))
      : [null, null];

    return rooms.filter((room) => {
      const price = Number(room.price || 0);
      const roomCapacity = Number(room.capacity || 0);

      if (filters.floor && `${room.floor}` !== filters.floor) return false;
      if (capacity !== null && roomCapacity < capacity) return false;
      if (minPrice !== null && price < minPrice) return false;
      if (maxPrice !== null && price > maxPrice) return false;
      return true;
    });
  }, [rooms, filters]);

  const handleFilterChange = (field, value) => {
    setFilters((current) => ({ ...current, [field]: value }));
  };

  const resetFilters = () => {
    setFilters({
      floor: '',
      capacity: '',
      priceRange: '',
    });
  };

  const resetSearch = () => {
    setCheckInDate(today);
    setCheckOutDate(tomorrow);
    setActiveDate('checkIn');
    setCalendarWheelLocked(false);
    resetFilters();
  };

  return (
    <div className="rooms-page">
      {/* 搜索区域 - 胶囊设计 */}
      <section className="search-section">
        <div className="search-container">
          <button type="button" className="search-reset-btn" onClick={resetSearch}>
            重置
          </button>

          <div className="date-search-pane">
            <div className="search-titlebar">
              <div>
                <p className="section-kicker">STAY DATES</p>
                <h2>选择入住日期</h2>
              </div>
              {searched && rooms.length > 0 && (
                <span className="available-pill">{filteredRooms.length} / {rooms.length} 间可订</span>
              )}
            </div>

            <div className="range-input-shell">
                <button
                  type="button"
                  className={`range-input-segment ${activeDate === 'checkIn' ? 'is-active' : ''}`}
                onClick={() => {
                  setActiveDate('checkIn');
                  setCalendarWheelLocked(false);
                }}
                >
                <span className="range-label">入住日期</span>
                <strong>{formatDate(checkInDate, 'YYYY/MM/DD')}</strong>
              </button>
              <span className="range-arrow">→</span>
                <button
                  type="button"
                  className={`range-input-segment ${activeDate === 'checkOut' ? 'is-active' : ''}`}
                onClick={() => {
                  setActiveDate('checkOut');
                  setCalendarWheelLocked(false);
                }}
                >
                <span className="range-label">离店日期</span>
                <strong>{formatDate(checkOutDate, 'YYYY/MM/DD')}</strong>
              </button>
              <span className="range-calendar-icon">▦</span>
            </div>

              <DateCalendar
                value={activeDate === 'checkIn' ? checkInDate : checkOutDate}
                min={activeDate === 'checkIn' ? today : dayjs(checkInDate || today).add(1, 'day').format('YYYY-MM-DD')}
                rangeStart={checkInDate}
                rangeEnd={checkOutDate}
                wheelLocked={calendarWheelLocked}
                onChange={(date) => {
                  if (activeDate === 'checkIn') {
                    setCheckInDate(date);
                  if (checkOutDate <= date) {
                    setCheckOutDate(dayjs(date).add(1, 'day').format('YYYY-MM-DD'));
                    }
                    setActiveDate('checkOut');
                    setCalendarWheelLocked(false);
                  } else {
                    setCheckOutDate(date);
                    setCalendarWheelLocked(true);
                  }
                }}
              />

            <div className="search-actions">
              {nights > 0 && (
                <div className="nights-capsule">
                  <span className="icon">🌙</span>
                  <span className="text">共 {nights} 晚</span>
                </div>
              )}

                <button
                  onClick={handleSearch}
                className="search-submit-btn"
                  disabled={loading}
                >
                {loading ? '查询中...' : '查询房间'}
              </button>
            </div>
          </div>

          <aside className="room-filter-pane">
            <FilterSelect
              label="楼层"
              value={filters.floor}
              options={floorSelectOptions}
              onChange={(value) => handleFilterChange('floor', value)}
            />

            <FilterSelect
              label="入住人数"
              value={filters.capacity}
              options={capacitySelectOptions}
              onChange={(value) => handleFilterChange('capacity', value)}
            />

            <FilterSelect
              label="价格档位"
              value={filters.priceRange}
              options={priceSelectOptions}
              onChange={(value) => handleFilterChange('priceRange', value)}
            />

            <div className="filter-summary">
              <span>当前结果</span>
              <strong>{searched ? filteredRooms.length : rooms.length} 间</strong>
            </div>
          </aside>
        </div>
      </section>

      {/* 房间列表 */}
      <section className="rooms-list">
        {loading ? (
          <div className="loading-container">
            <div className="loading-spinner"></div>
            <p className="loading-text">查询中...</p>
          </div>
        ) : searched ? (
          filteredRooms.length > 0 ? (
            <>
              <div className="list-header">
                <div>
                  <p className="section-kicker">AVAILABLE ROOMS</p>
                  <h2>可订房间</h2>
                </div>
                <p>{formatDate(checkInDate)} 至 {formatDate(checkOutDate)}，为您找到 {filteredRooms.length} 间符合条件的可订房间</p>
              </div>
              <div className="rooms-grid">
                {filteredRooms.map((room) => (
                  <div key={room.id} className="room-item cosmic-ui-card">
                    <div className="room-image">
                      {room.imageUrl ? (
                        <img src={room.imageUrl} alt={room.name} />
                      ) : (
                        <div className="room-placeholder">🌟</div>
                      )}
                    </div>

                    <div className="room-details">
                      <div className="room-header">
                        <div className="room-title">
                          <h3>{room.name}</h3>
                          <p className="room-number">房间号：{room.roomNo}</p>
                        </div>
                        <div className="room-price">
                          <div className="price-amount">¥{room.price}</div>
                          <div className="price-unit">/晚</div>
                        </div>
                      </div>

                      <div className="room-meta">
                        <span className="meta-item">👥 {room.capacity}人</span>
                        <span className="meta-item">📐 {room.area}㎡</span>
                        <span className="meta-item">🏢 {room.floor}</span>
                      </div>

                      {room.facilities && (
                        <div className="room-facilities">
                          <span>设施</span>
                          <p>{room.facilities}</p>
                        </div>
                      )}

                      <div className="room-footer">
                        <div className="total-price">
                          <span className="label">总价</span>
                          <span className="amount">¥{room.price * nights}</span>
                        </div>
                        <button
                          onClick={() => handleBook(room)}
                          className="cosmic-btn-primary book-btn"
                        >
                          立即预订
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </>
          ) : (
            <div className="empty-state">
              <div className="empty-icon">😔</div>
              <h3>{rooms.length > 0 ? '暂无符合筛选的房间' : '暂无可订房间'}</h3>
              <p>{rooms.length > 0 ? '可以放宽楼层、价格或人数条件再试试' : '该时间段暂无可订房间，请尝试其他日期'}</p>
            </div>
          )
        ) : (
          <div className="empty-state">
            <div className="empty-icon">📅</div>
            <h3>请选择入住日期查询可订房间</h3>
            <p>选择入住和离店日期后，点击"查询房间"按钮</p>
          </div>
        )}
      </section>
    </div>
  );
}

export default Rooms;
