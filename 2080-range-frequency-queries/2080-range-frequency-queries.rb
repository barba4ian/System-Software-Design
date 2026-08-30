class RangeFreqQuery

    def initialize a
        @h = h = Hash.new { _1[_2] = [] }
        a.each_with_index { h[_1] << _2 }
    end

    def query l, r, v
        i = (a = @h[v]).bsearch { _1 >= l }
        return 0 if !i || i > r
        i = a.bsearch_index { _1 >= l }
        j = a.bsearch_index { _1 >  r }
        j ? j - i : a.size - i
    end

end