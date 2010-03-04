
#ifndef HG_SMARTPOINTER
#define HG_SMARTPOINTER

#include <assert.h>
#include "jasp_exception.h"

template <class T> class CountedSpRef;

template <class T>
class CountedSp
{

private:

	CountedSpRef<T>* m_pCounted;
	
	void UnBind()
	{
		if (!Null() && m_pCounted->DecRef() == 0)
		{
			delete m_pCounted;
		}
		m_pCounted = 0;
	}

public:

	CountedSp() : m_pCounted(0)
	{
	}

	CountedSp(T* pT)
	{
		m_pCounted = new CountedSpRef<T>(pT);
		m_pCounted->IncRef();
	}

	~CountedSp()
	{
		UnBind();
	}

	CountedSp(const CountedSp<T>& rVar)
	{
		m_pCounted = rVar.m_pCounted;
		if (!Null())
		{
			m_pCounted->IncRef();
		}
	}

	CountedSp<T>& operator=(const CountedSp<T>& rVar)
	{
		if (!rVar.Null())
		{
			rVar.m_pCounted->IncRef();		
		}
		UnBind();
		m_pCounted = rVar.m_pCounted;
		return *this;
	}

	T* operator->()
	{
		if (Null())
		{
			throw JaspNullRefExc();
		}
		return m_pCounted->m_pT;
	}

	const T* operator->() const
	{
		if (Null())
		{
			throw JaspNullRefExc();
		}
		return m_pCounted->m_pT;
	}

	friend bool operator==(const CountedSp<T>& lhs, const CountedSp<T>& rhs)
	{
		return lhs.m_pCounted->m_pT == rhs.m_pCounted->m_pT;
	}

	friend bool operator!=(const CountedSp<T>& lhs, const CountedSp<T>& rhs)
	{
		return !(lhs == rhs);
	}

	const T* getPointer() const
	{
		return m_pCounted->m_pT;
	}
	
	T* getPointer()
	{
		return m_pCounted->m_pT;
	}

	bool Null() const
	{
		return m_pCounted == 0;
	}

	void SetNull()
	{
		UnBind();
	}

};

template <class T>
class CountedSpRef
{

	friend class CountedSp<T>;

private:

	T* const m_pT;
	unsigned m_refCount;

	CountedSpRef(T* pT) : m_refCount(0), m_pT(pT)
	{
		assert(pT != 0);
	}

	~CountedSpRef()
	{
		assert(m_refCount == 0);
		delete m_pT;
	}

	CountedSpRef<T>& operator=( const CountedSpRef<T>& )
	{
		assert(false);
		return 0;
	}

	unsigned IncRef()
	{
		return ++m_refCount;
	}

	unsigned DecRef()
	{
		assert(m_refCount > 0);
		return --m_refCount;
	}

};

#endif
